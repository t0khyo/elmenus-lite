package spring.practice.elmenus_lite.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.InvalidOrderException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.Restaurant;
import spring.practice.elmenus_lite.repostory.RestaurantRepository;

@Service
@RequiredArgsConstructor
public class RestaurantUtils {
    private final RestaurantRepository restaurantRepository;

    public Restaurant fetchAndValidateRestaurant(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getFinalMessage(restaurantId)));

        if (!restaurant.isActive()) {
            throw new InvalidOrderException(ErrorMessage.RESTAURANT_INACTIVE.getFinalMessage());
        }
        return restaurant;
    }
}
