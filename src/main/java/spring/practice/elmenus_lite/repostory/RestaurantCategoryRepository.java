package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.RestaurantCategory;

import java.util.Optional;

@Repository
public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, RestaurantCategory.RestaurantCategoryId> {
    // Find a specific association by restaurant ID and category ID
    Optional<RestaurantCategory> findByIdRestaurantIdAndIdCategoryId(Integer restaurantId, Integer categoryId);
}