package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.Menu;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    List<Menu> findByRestaurantId(Integer restaurantId);

    Optional<Menu> findByIdAndRestaurantId(Integer menuId, Integer restaurantId);

    Optional<Menu> findByNameAndRestaurantId(String name, Integer restaurantId);

    Optional<Menu> findByNameAndIdNotAndRestaurantId(String name, Integer menuId, Integer restaurantId);
}