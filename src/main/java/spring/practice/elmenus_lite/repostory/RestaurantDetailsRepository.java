package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.RestaurantDetails;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantDetailsRepository extends JpaRepository<RestaurantDetails, Integer> {

  Optional<RestaurantDetails> findByRestaurantId(Integer restaurantId);

  @Query(
          value = "SELECT restaurant_details_id FROM restaurant_details WHERE review_count > 0 ORDER BY average_rating DESC LIMIT 10",
          nativeQuery = true
  )
  List<Integer> findTop10RestaurantIdsNative();

}