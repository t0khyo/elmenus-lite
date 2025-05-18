// repository/CartRepository.java
package com.elmenus_lite.repository;

import com.elmenus_lite.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByCustomerId(Integer customerId);
}