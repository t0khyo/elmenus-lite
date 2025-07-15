package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.request.ReviewRequest;

public interface ReviewService {

    Integer createReview(Integer restaurantId, Integer customerId, ReviewRequest request);

    void deleteReview(Integer reviewId);
}
