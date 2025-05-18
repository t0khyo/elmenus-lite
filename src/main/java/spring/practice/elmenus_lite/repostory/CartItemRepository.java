// repository/CartItemRepository.java
package com.elmenus_lite.repository;

import com.elmenus_lite.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}