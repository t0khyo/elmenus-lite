// repository/OrderRepository.java
package com.elmenus_lite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elmenus_lite.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerId(Integer customerId);
    List<Order> findByCustomerIdOrderByOrderDateDesc(Integer customerId);
}