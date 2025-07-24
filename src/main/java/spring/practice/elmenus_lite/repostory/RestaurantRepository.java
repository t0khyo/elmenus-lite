package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.Restaurant;

import java.util.List;
import java.util.Optional;

@Repository
public interface  RestaurantRepository extends JpaRepository<Restaurant, Integer>, JpaSpecificationExecutor<Restaurant> {



    boolean existsByName(String name);

    // Find a restaurant by its name, excluding a specific ID, useful for update uniqueness check
    Optional<Restaurant> findByNameAndIdNot(String restaurantName, Integer restaurantId);

    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "LEFT JOIN r.restaurantDetails rd " +
            "LEFT JOIN r.restaurantCategories rc " +
            "LEFT JOIN rc.category c " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(rd.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> searchRestaurantsByKeyword(@Param("keyword") String keyword);

}