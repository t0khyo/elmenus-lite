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
import org.springframework.util.StringUtils;
import spring.practice.elmenus_lite.dto.request.RestaurantRequest;
import spring.practice.elmenus_lite.dto.response.RestaurantResponse;
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
    public void addRestaurant(RestaurantRequest request) {
        log.info("Attempting to create a new restaurant with name: {}", request.getName());

        // Business validation: Check for unique restaurant name
        validateUniqueRestaurantName(request);

        // Map DTO to Entity for Restaurant. RestaurantDetails is created within the mapper.
        Restaurant restaurant = mapToRestaurant(request);

        // Save the restaurant. Due to CascadeType.ALL on restaurantDetails, it will be saved too.
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Successfully created restaurant with ID: {}", savedRestaurant.getId());
    }

    @Override
    public List<RestaurantResponse> getRestaurantsByFilters(String category, String name, Time time, Integer minRating, Integer page, Integer size) {
        log.info("Retrieving restaurants with filters - category: {}, name: {}, time: {}, minRating: {}, page: {}, size: {}",
                category, name, time, minRating, page, size);


        // Build Specification for dynamic querying
        Specification<Restaurant> spec = buildRestaurantSpecification(category, name, time);


        Pageable pageable = PageRequest.of(page, size);
        List<Restaurant> restaurants = restaurantRepository.findAll(spec, pageable).getContent();
        log.info("Applied pagination: page {}, size {}", page, size);
        List<RestaurantResponse> responses = restaurants.stream()
                .map(restaurantMapper::toRestaurantResponse)
                .collect(Collectors.toList());

        return responses;
    }

    private  List<RestaurantResponse> filterResponsesByRating(Integer minRating, List<RestaurantResponse> responses, Map<Integer, Double> avgRatingsMap) {
        return responses.stream()
                .map(r -> {
                    r.setAverageRating(avgRatingsMap.getOrDefault(r.getId(), 0.0));
                    return r;
                }) // Set avg rating
                .filter(r -> r.getAverageRating() >= minRating) // Filter by minRating
                .collect(Collectors.toList());
    }

    private Map<Integer, Double> getAvgRatingsMap() {
        List<Object[]> avgRatingsData = reviewRepository.findAverageRatingsGroupedByRestaurant();
        Map<Integer, Double> avgRatingsMap = avgRatingsData.stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0], // restaurantId
                        arr -> ((Number) arr[1]).doubleValue() // average rating
                ));
        return avgRatingsMap;
    }

    private Specification<Restaurant> buildRestaurantSpecification(String category, String name, Time time) {
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
        return spec;
    }

    @Override
    public List<RestaurantResponse> searchRestaurants(String keyword) {
        log.info("Searching restaurants with keyword: {}", keyword);
        if (keyword == null || keyword.isBlank()) {
            throw new InvalidInputException("Search keyword cannot be empty.");
        }

        // full text search
        List<Restaurant> restaurants = restaurantRepository.searchRestaurantsByKeyword(keyword);

        List<RestaurantResponse> responses = restaurants.stream()
                .map(restaurantMapper::toRestaurantResponse)
                .collect(Collectors.toList());

        log.info("Found {} restaurants matching keyword '{}'", responses.size(), keyword);
        return responses;
    }

    @Override
    public List<RestaurantResponse> getTopRatedRestaurants() {

        List<Integer> RestaurantIds = restaurantDetailsRepository.findTop10RestaurantIdsNative();

        if (RestaurantIds.isEmpty()) {
            throw new ResourceNotFoundException("No restaurants found with sufficient review data to determine top ratings.");
        }


        // Fetch full Restaurant entities for the top-rated IDs
        List<Restaurant> topRatedRestaurants = restaurantRepository.findAllById(RestaurantIds);

        // Map entities to DTOs and set their average ratings
        List<RestaurantResponse> responses = topRatedRestaurants.stream()
                .map(restaurantMapper::toRestaurantResponse)
                .collect(Collectors.toList());

        log.info("Found {} top-rated restaurants.", responses.size());
        return responses;

    }

    @Override
    public RestaurantResponse getRestaurant(Integer restaurantId) {
        log.info("Retrieving restaurant with ID: {}", restaurantId);
        Restaurant restaurant = getExistingRestaurant(restaurantId);

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
        Restaurant existingRestaurant = getExistingRestaurant(restaurantId);
        //creating details if they don't exist & update it if exits
        RestaurantDetails restaurantDetails = buildRestaurantDetails(existingRestaurant, request);
        restaurantDetailsRepository.save(restaurantDetails); // Explicitly save details if new or just updated its own fields
        restaurantRepository.save(existingRestaurant); // Save the main restaurant entity
        log.info("Restaurant with ID: {} updated successfully.", restaurantId);
    }

    @Override
    public void assignCategoryToRestaurant(Integer restaurantId, Integer categoryId) {
        log.info("Assigning category ID {} to restaurant ID {}.", categoryId, restaurantId);

        if (restaurantCategoryRepository.findByIdRestaurantIdAndIdCategoryId(restaurantId, categoryId).isPresent()) {
            log.warn("Category ID {} is already assigned to restaurant ID {}. Skipping assignment.", categoryId, restaurantId);
            throw new DuplicateResourceException("Category ID "+categoryId+" is already assigned to restaurant ID " + restaurantId);
        }

        Restaurant restaurant = getExistingRestaurant(restaurantId);
        Category category = getExistingCategory(categoryId);

        RestaurantCategory restaurantCategory = buildRestaurantCategory(restaurantId, categoryId, restaurant, category);

        restaurantCategoryRepository.save(restaurantCategory);
        log.info("Successfully assigned category ID {} to restaurant ID {}.", categoryId, restaurantId);
    }

    private  RestaurantCategory buildRestaurantCategory(Integer restaurantId, Integer categoryId, Restaurant restaurant, Category category) {
        return RestaurantCategory.builder()
                .id(new RestaurantCategory.RestaurantCategoryId(restaurantId, categoryId))
                .restaurant(restaurant)
                .category(category)
                .build();
    }

    private Category getExistingCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    @Override
    public void removeCategoryFromRestaurant(Integer restaurantId, Integer categoryId) {
        log.info("Removing category ID {} from restaurant ID {}.", categoryId, restaurantId);

        // Check if restaurant and category exist
        getExistingRestaurant(restaurantId);
        getExistingCategory(categoryId);

        // Check if restaurant have this category before attempting to delete
        RestaurantCategory.RestaurantCategoryId id = new RestaurantCategory.RestaurantCategoryId(restaurantId, categoryId);
        RestaurantCategory association = restaurantCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category ID " + categoryId + " is not assigned to restaurant ID " + restaurantId));

        restaurantCategoryRepository.delete(association);
        log.info("Successfully removed category ID {} from restaurant ID {}.", categoryId, restaurantId);
    }

    private void validateUniqueRestaurantName(RestaurantRequest request) {
        if (restaurantRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Restaurant with name '" + request.getName() + "' already exists.");
        }
    }

    private Restaurant mapToRestaurant(RestaurantRequest request) {

        Restaurant restaurant = restaurantMapper.toRestaurantEntity(request);
        // Link RestaurantDetails to Restaurant after mapping
        RestaurantDetails details = restaurant.getRestaurantDetails();
        if (details != null) {
            details.setRestaurant(restaurant); // Ensure bi-directional link
        } else {
            // This should ideally not happen if mapper's default method is robust
            throw new InvalidInputException("Restaurant details (location, times) cannot be null during creation.");
        }
        return restaurant ;
    }

    private Restaurant getExistingRestaurant(Integer restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
    }

    private RestaurantDetails buildRestaurantDetails(Restaurant existingRestaurant, RestaurantRequest request) {

        // update name
        if(StringUtils.hasText(request.getName())) {
            validateUniqueRestaurantName(request);
            existingRestaurant.setName(request.getName());
        }

        RestaurantDetails details = existingRestaurant.getRestaurantDetails();
        if (details == null) {
            details = RestaurantDetails.buildRestaurantDetails(existingRestaurant);
            existingRestaurant.setRestaurantDetails(details);
        }

        if (request.getDescription() != null) details.setDescription(request.getDescription());
        if (request.getPhone() != null) details.setPhone(request.getPhone());
        if (request.getOpenTime() != null) details.setOpenTime(request.getOpenTime());
        if (request.getCloseTime() != null) details.setCloseTime(request.getCloseTime());

        return details;
    }

}
