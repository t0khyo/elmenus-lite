package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.request.RestaurantRequest;
import spring.practice.elmenus_lite.dto.response.RestaurantResponse;

import java.sql.Time;
import java.util.List;

public interface RestaurantService {
    void addRestaurant(RestaurantRequest request);

    List<RestaurantResponse> getRestaurantsByFilters(
            String category, String name, Time time, Integer minRating, Integer page, Integer size);

    List<RestaurantResponse> searchRestaurants(String keyword);

    List<RestaurantResponse> getTopRatedRestaurants();

    RestaurantResponse getRestaurant(Integer restaurantId);

    void updateRestaurantProfile(Integer restaurantId, RestaurantRequest request);

    void assignCategoryToRestaurant(Integer restaurantId, Integer categoryId);

    void removeCategoryFromRestaurant(Integer restaurantId, Integer categoryId);
}
