package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.practice.elmenus_lite.model.RestaurantDetails;

import java.util.Optional;

public interface RestaurantDetailsRepository extends JpaRepository<RestaurantDetails, Integer> {

  Optional<RestaurantDetails> findByRestaurantId(Integer restaurantId);

}