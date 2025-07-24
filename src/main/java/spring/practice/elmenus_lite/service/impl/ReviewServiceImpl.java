package spring.practice.elmenus_lite.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.request.ReviewRequest;
import spring.practice.elmenus_lite.exception.InvalidInputException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.mapper.ReviewMapper;
import spring.practice.elmenus_lite.model.Customer;
import spring.practice.elmenus_lite.model.Restaurant;
import spring.practice.elmenus_lite.model.RestaurantDetails;
import spring.practice.elmenus_lite.model.Review;
import spring.practice.elmenus_lite.repostory.CustomerRepository;
import spring.practice.elmenus_lite.repostory.RestaurantDetailsRepository;
import spring.practice.elmenus_lite.repostory.RestaurantRepository;
import spring.practice.elmenus_lite.repostory.ReviewRepository;
import spring.practice.elmenus_lite.service.ReviewService;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantDetailsRepository restaurantDetailsRepository;
    private final ReviewMapper reviewMapper;


    @Override
    public Integer createReview(Integer restaurantId, Integer customerId, ReviewRequest request) {
        log.info("Creating review for restaurant ID: {} by customer ID: {}", restaurantId, customerId);

        Restaurant restaurant = getRestaurant(restaurantId);
        Customer customer = getCustomer(customerId);
        Review review = mapToReview(request, restaurant, customer);

        Review savedReview = reviewRepository.save(review);
        log.info("Review ID: {} saved. Recalculating restaurant average rating and review count.", savedReview.getId());

        // Recalculate and update denormalized fields
        updateRestaurantAverageRatingAndCount(restaurantId);

        return savedReview.getId();
    }



    @Override
    public void deleteReview(Integer reviewId) {
        log.info("Deleting review with ID: {}", reviewId);
        Review reviewToDelete = getExistingReview(reviewId);

        Integer restaurantId = reviewToDelete.getRestaurant().getId();
        reviewRepository.delete(reviewToDelete);
        log.info("Review ID: {} deleted. Recalculating restaurant average rating and review count.", reviewId);

        // Recalculate and update denormalized fields
        updateRestaurantAverageRatingAndCount(restaurantId);
    }


    private void updateRestaurantAverageRatingAndCount(Integer restaurantId) {
        log.debug("Starting recalculation for restaurant ID: {}", restaurantId);

        // Fetch all reviews for the restaurant
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);

        double newAverageRating = 0.0;
        int newReviewCount = reviews.size();

        if (newReviewCount > 0) {
            newAverageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0); // Should not happen if count > 0
        }

        // Find the RestaurantDetails entity to update
        RestaurantDetails restaurantDetails = getRestaurantDetails(restaurantId);

        // Update the fields
        restaurantDetails.setAverageRating(newAverageRating);
        restaurantDetails.setReviewCount(newReviewCount);

        // Save the updated RestaurantDetails
        restaurantDetailsRepository.save(restaurantDetails);
        log.info("Recalculated for restaurant ID {}: averageRating={}, reviewCount={}", restaurantId, newAverageRating, newReviewCount);
    }

    private Review mapToReview(ReviewRequest request, Restaurant restaurant, Customer customer) {
        Review review = reviewMapper.toReviewEntity(request);
        review.setRestaurant(restaurant);
        review.setCustomer(customer);
        return review;
    }

    private RestaurantDetails getRestaurantDetails(Integer restaurantId) {
        return restaurantDetailsRepository.findByRestaurantId(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantDetails not found for restaurant ID: " + restaurantId));
    }

    private Customer getCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
    }

    private Restaurant getRestaurant(Integer restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
    }

    private Review getExistingReview(Integer reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
    }}
