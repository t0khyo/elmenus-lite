package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.RestaurantDetails;

import java.util.Optional;

@Repository
public interface RestaurantDetailsRepository extends JpaRepository<RestaurantDetails, Integer> {

  Optional<RestaurantDetails> findByRestaurantId(Integer restaurantId);

}