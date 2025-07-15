package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.practice.elmenus_lite.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByRestaurantId(Integer restaurantId);

    @Query(value = "SELECT r.restaurant.id, AVG(r.rating) FROM Review r GROUP BY r.restaurant.id")
    List<Object[]> findAverageRatingsGroupedByRestaurant();

}