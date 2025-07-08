package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.request.RestaurantRequest;
import spring.practice.elmenus_lite.dto.response.RestaurantResponse;

import java.sql.Time;
import java.util.List;

public interface RestaurantService {
    Integer createRestaurant(RestaurantRequest request);

    List<RestaurantResponse> getRestaurants(
            String category, String name, Time time, Integer minRating, Integer page, Integer size);

    List<RestaurantResponse> searchRestaurants(String keyword);

    List<RestaurantResponse> getTopRatedRestaurants(Integer limit);

    RestaurantResponse getRestaurantById(Integer restaurantId);

    void updateRestaurantProfile(Integer restaurantId, RestaurantRequest request);

    void assignCategoryToRestaurant(Integer restaurantId, Integer categoryId);

    void removeCategoryFromRestaurant(Integer restaurantId, Integer categoryId);
}
