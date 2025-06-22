package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.Restaurant;

import java.util.Optional;

@Repository
public interface  RestaurantRepository extends JpaRepository<Restaurant, Integer>, JpaSpecificationExecutor<Restaurant> {

    // Find a restaurant by its name, useful for checking uniqueness during creation/update
    Optional<Restaurant> findByName(String restaurantName);

    // Find a restaurant by its name, excluding a specific ID, useful for update uniqueness check
    Optional<Restaurant> findByNameAndIdNot(String restaurantName, Integer restaurantId);

}