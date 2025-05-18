// repository/MenuItemRepository.java
package com.elmenus_lite.repository;

import com.elmenus_lite.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByMenuId(Integer menuId);
    List<MenuItem> findByMenuIdAndAvailableTrue(Integer menuId);
}