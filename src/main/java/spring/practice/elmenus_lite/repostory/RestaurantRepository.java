// repository/RestaurantRepository.java
package com.elmenus_lite.repository;

import com.elmenus_lite.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    List<Restaurant> findByNameContainingIgnoreCase(String name);

    @Query("SELECT r FROM Restaurant r JOIN r.categories rc WHERE rc.category.id = :categoryId")
    List<Restaurant> findByCategoryId(Integer categoryId);
}