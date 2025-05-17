// repository/MenuItemRepository.java
package com.javaeats.repository;

import com.javaeats.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByMenuId(Integer menuId);
    List<MenuItem> findByMenuIdAndAvailableTrue(Integer menuId);
}