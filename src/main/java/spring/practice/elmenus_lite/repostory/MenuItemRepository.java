package spring.practice.elmenus_lite.repostory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.MenuItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> , JpaSpecificationExecutor<MenuItem> {

    List<MenuItem> findByMenuId(Integer menuId);


    Optional<MenuItem> findByIdAndMenuId(Integer menuItemId, Integer menuId);


    List<MenuItem> findByMenuRestaurantId(Integer restaurantId);


    @Query("SELECT mi FROM MenuItem mi WHERE " +
            "(LOWER(mi.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    Page<MenuItem> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);


    Optional<MenuItem> findByNameAndMenuId(String name, Integer menuId);

    Optional<MenuItem> findByNameAndIdNotAndMenuId(String name, Integer menuItemId, Integer menuId);
}