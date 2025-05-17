// repository/OrderRepository.java
package com.javaeats.repository;

import com.javaeats.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerId(Integer customerId);
    List<Order> findByCustomerIdOrderByOrderDateDesc(Integer customerId);
}