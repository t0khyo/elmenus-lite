// repository/CustomerRepository.java
package com.elmenus_lite.repository;

import com.elmenus_lite.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findByUserEmail(String email);
}