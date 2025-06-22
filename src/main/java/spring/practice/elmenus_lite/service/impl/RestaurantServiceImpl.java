package spring.practice.elmenus_lite.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.RestaurantRequest;
import spring.practice.elmenus_lite.dto.RestaurantResponse;
import spring.practice.elmenus_lite.exception.DuplicateResourceException;
import spring.practice.elmenus_lite.exception.InvalidInputException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.mapper.RestaurantMapper;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.repostory.*;
import spring.practice.elmenus_lite.service.RestaurantService;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j // For logging
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantDetailsRepository restaurantDetailsRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantMapper restaurantMapper;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 RestaurantDetailsRepository restaurantDetailsRepository,
                                 CategoryRepository categoryRepository,
                                 RestaurantCategoryRepository restaurantCategoryRepository,
                                 ReviewRepository reviewRepository,
                                 RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantDetailsRepository = restaurantDetailsRepository;
        this.categoryRepository = categoryRepository;
        this.restaurantCategoryRepository = restaurantCategoryRepository;
        this.reviewRepository = reviewRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public Integer createRestaurant(RestaurantRequest request) {
        log.info("Attempting to create a new restaurant with name: {}", request.name());

        // Business validation: Check for unique restaurant name
        if (restaurantRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateResourceException("Restaurant with name '" + request.name() + "' already exists.");
        }

        // Map DTO to Entity for Restaurant. RestaurantDetails is created within the mapper.
        Restaurant restaurant = restaurantMapper.toRestaurantEntity(request);
//        restaurant.setActive(true); // Default to active for new restaurants

        // Link RestaurantDetails to Restaurant after mapping
        RestaurantDetails details = restaurant.getRestaurantDetails();
        if (details != null) {
            details.setRestaurant(restaurant); // Ensure bi-directional link
        } else {
            // This should ideally not happen if mapper's default method is robust
            throw new InvalidInputException("Restaurant details (location, times) cannot be null during creation.");
        }

        // Save the restaurant. Due to CascadeType.ALL on restaurantDetails, it will be saved too.
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Successfully created restaurant with ID: {}", savedRestaurant.getId());
        return savedRestaurant.getId();
    }

    @Override
    public List<RestaurantResponse> getRestaurants(String category, String name, Time time, Integer minRating, Integer page, Integer size) {
        log.info("Retrieving restaurants with filters - category: {}, name: {}, time: {}, minRating: {}, page: {}, size: {}",
                category, name, time, minRating, page, size);


        // Build Specification for dynamic querying
        Specification<Restaurant> spec = Specification.where(null);

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (category != null && !category.isBlank()) {
            spec = spec.and((root, query, cb) -> {
                Join<Restaurant, RestaurantCategory> restaurantCategoryJoin = root.join("restaurantCategories", JoinType.INNER);
                Join<RestaurantCategory, Category> categoryJoin = restaurantCategoryJoin.join("category", JoinType.INNER);
                return cb.equal(cb.lower(categoryJoin.get("name")), category.toLowerCase());
            });
        }

        if (time != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Restaurant, RestaurantDetails> detailsJoin = root.join("restaurantDetails", JoinType.INNER);
                return cb.and(
                        cb.lessThanOrEqualTo(detailsJoin.get("openTime"), time),
                        cb.greaterThanOrEqualTo(detailsJoin.get("closeTime"), time)
                );
            });
        }
        List<Restaurant> restaurants;
        if (page != null && size != null && page >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            restaurants = restaurantRepository.findAll(spec, pageable).getContent();
            log.info("Applied pagination: page {}, size {}", page, size);
        }
        else {
            // If no valid pagination parameters, fetch all matching results
            restaurants = restaurantRepository.findAll(spec);
            log.info("No pagination applied. Fetched {} restaurants.", restaurants.size());
        }

        List<RestaurantResponse> responses = restaurants.stream()
                .map(restaurantMapper::toRestaurantResponse)
                .collect(Collectors.toList());

        // Apply minRating filtering and average rating calculation post-query
        // This is done after fetching the primary results
        if (minRating != null) {
            if (minRating < 1 || minRating > 5) {
                throw new InvalidInputException("Minimum rating must be between 1 and 5.");
            }
            List<Object[]> avgRatingsData = reviewRepository.findAverageRatingsByRestaurant(1L); // Assume at least 1 review to calculate avg
            Map<Integer, Double> avgRatingsMap = avgRatingsData.stream()
                    .collect(Collectors.toMap(
                            arr -> (Integer) arr[0], // restaurantId
                            arr -> ((Number) arr[1]).doubleValue() // average rating
                    ));

            responses = responses.stream()
                    .map(r -> {
                        r.setAverageRating(avgRatingsMap.getOrDefault(r.getId(), 0.0));
                        return r;
                    }) // Set avg rating
                    .filter(r -> r.getAverageRating() >= minRating) // Filter by minRating
                    .collect(Collectors.toList());
            log.info("Applied minimum rating filter: {}", minRating);
        }
        return responses;
    }

    @Override
    public List<RestaurantResponse> searchRestaurants(String keyword) {
        log.info("Searching restaurants with keyword: {}", keyword);
        if (keyword == null || keyword.isBlank()) {
            throw new InvalidInputException("Search keyword cannot be empty.");
        }

        Specification<Restaurant> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));

            Join<Restaurant, RestaurantDetails> detailsJoin = root.join("restaurantDetails", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(detailsJoin.get("description")), "%" + keyword.toLowerCase() + "%"));

            Join<Restaurant, RestaurantCategory> restaurantCategoryJoin = root.join("restaurantCategories", JoinType.LEFT);
            Join<RestaurantCategory, Category> categoryJoin = restaurantCategoryJoin.join("category", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(categoryJoin.get("name")), "%" + keyword.toLowerCase() + "%"));

            assert query != null;
            query.distinct(true);

            return cb.or(predicates.toArray(new Predicate[0]));
        };

        List<Restaurant> restaurants = restaurantRepository.findAll(spec);
        List<RestaurantResponse> responses = restaurants.stream()
                .map(restaurantMapper::toRestaurantResponse)
                .collect(Collectors.toList());

        if (!responses.isEmpty()) {
            List<Object[]> avgRatingsData = reviewRepository.findAverageRatingsByRestaurant(1L);
            Map<Integer, Double> avgRatingsMap = avgRatingsData.stream()
                    .collect(Collectors.toMap(
                            arr -> (Integer) arr[0],
                            arr -> ((Number) arr[1]).doubleValue()
                    ));
            responses.forEach(r -> r.setAverageRating(avgRatingsMap.getOrDefault(r.getId(), 0.0)));
        }

        log.info("Found {} restaurants matching keyword '{}'", responses.size(), keyword);
        return responses;
    }

    @Override
    public List<RestaurantResponse> getTopRatedRestaurants(Integer limit) {
        log.info("Retrieving top {} rated restaurants.", limit);
        if (limit == null || limit <= 0) {
            throw new InvalidInputException("Limit must be a positive integer.");
        }

        // Fetch average ratings for all restaurants that have at least one review.
        // The query returns List<Object[]> where each Object[] is [restaurantId, averageRating]
        List<Object[]> avgRatingsData = reviewRepository.findAverageRatingsByRestaurant(1L); // min 1 review

        if (avgRatingsData.isEmpty()) {
            throw new ResourceNotFoundException("No restaurants found with sufficient review data to determine top ratings.");
        }

        // Convert the raw data to a map for easy lookup
        Map<Integer, Double> restaurantRatingsMap = avgRatingsData.stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0], // restaurantId
                        arr -> ((Number) arr[1]).doubleValue() // average rating
                ));

        // Get the IDs of the top-rated restaurants based on the map and limit
        List<Integer> topRatedRestaurantIds = restaurantRatingsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // Sort by rating descending
                .limit(limit) // Apply the limit
                .map(Map.Entry::getKey) // Get only the restaurant IDs
                .collect(Collectors.toList());

        // Fetch full Restaurant entities for the top-rated IDs
        List<Restaurant> topRatedRestaurants = restaurantRepository.findAllById(topRatedRestaurantIds);

        // Map entities to DTOs and set their average ratings
        List<RestaurantResponse> responses = topRatedRestaurants.stream()
                .map(restaurantMapper::toRestaurantResponse)
                .peek(dto -> dto.setAverageRating(restaurantRatingsMap.get(dto.getId()))) // Set the pre-calculated avg rating
                .sorted(Comparator.comparing(RestaurantResponse::getAverageRating, Comparator.reverseOrder())) // Re-sort to ensure correct order
                .collect(Collectors.toList());

        log.info("Found {} top-rated restaurants.", responses.size());
        return responses;

    }

    @Override
    public RestaurantResponse getRestaurantById(Integer restaurantId) {
        log.info("Retrieving restaurant with ID: {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        RestaurantResponse response = restaurantMapper.toRestaurantResponse(restaurant);

        // Calculate and set average rating for single restaurant view
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            response.setAverageRating(avgRating);
        } else {
            response.setAverageRating(0.0); // No reviews yet
        }

        log.info("Successfully retrieved restaurant with ID: {}", restaurantId);
        return response;
    }

    @Override
    public void updateRestaurantProfile(Integer restaurantId, RestaurantRequest request) {
        log.info("Attempting to update restaurant with ID: {}", restaurantId);

        // Basic validation for update operation
        if (request.name() == null && request.description() == null &&
                request.phone() == null && request.openTime() == null &&
                request.closeTime() == null ) {
            throw new InvalidInputException("At least one field (name, description, phone, openTime, closeTime, active) must be provided for update.");
        }

        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        // Business validation: Check for unique name if name is being updated
        if (request.name() != null && !request.name().isBlank() &&
                !request.name().equals(existingRestaurant.getName())) {
            // make sure it is unique name
            if (restaurantRepository.findByNameAndIdNot(request.name(), restaurantId).isPresent()) {
                throw new DuplicateResourceException("Restaurant with name '" + request.name() + "' already exists.");
            }
            existingRestaurant.setName(request.name());
        }

        //creating details if they don't exist:
        RestaurantDetails details = existingRestaurant.getRestaurantDetails();
        if (details == null) {
            details = RestaurantDetails.builder()
                    .restaurant(existingRestaurant)
                    .id(existingRestaurant.getId())
                    .build();
            existingRestaurant.setRestaurantDetails(details);
        }

        if (request.description() != null) details.setDescription(request.description());
        if (request.phone() != null) details.setPhone(request.phone());
        if (request.openTime() != null) details.setOpenTime(request.openTime());
        if (request.closeTime() != null) details.setCloseTime(request.closeTime());
        restaurantDetailsRepository.save(details); // Explicitly save details if new or just updated its own fields


        restaurantRepository.save(existingRestaurant); // Save the main restaurant entity
        log.info("Restaurant with ID: {} updated successfully.", restaurantId);
    }

    @Override
    public void assignCategoryToRestaurant(Integer restaurantId, Integer categoryId) {
        log.info("Assigning category ID {} to restaurant ID {}.", categoryId, restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        // Check if association already exists (idempotency)
        if (restaurantCategoryRepository.findByIdRestaurantIdAndIdCategoryId(restaurantId, categoryId).isPresent()) {
            log.warn("Category ID {} is already assigned to restaurant ID {}. Skipping assignment.", categoryId, restaurantId);
            return; // Or throw a specific "AlreadyExists" exception if preferred
        }


        RestaurantCategory restaurantCategory = RestaurantCategory.builder()
                .id(new RestaurantCategory.RestaurantCategoryId(restaurantId, categoryId))
                .restaurant(restaurant)
                .category(category)
                .build();

        restaurantCategoryRepository.save(restaurantCategory);
        log.info("Successfully assigned category ID {} to restaurant ID {}.", categoryId, restaurantId);
    }

    @Override
    public void removeCategoryFromRestaurant(Integer restaurantId, Integer categoryId) {
        log.info("Removing category ID {} from restaurant ID {}.", categoryId, restaurantId);

        // Check if restaurant and category exist
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        // Check if restaurant have this category before attempting to delete
        RestaurantCategory.RestaurantCategoryId id = new RestaurantCategory.RestaurantCategoryId(restaurantId, categoryId);
        RestaurantCategory association = restaurantCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category ID " + categoryId + " is not assigned to restaurant ID " + restaurantId));

        restaurantCategoryRepository.delete(association);
        log.info("Successfully removed category ID {} from restaurant ID {}.", categoryId, restaurantId);
    }
}
