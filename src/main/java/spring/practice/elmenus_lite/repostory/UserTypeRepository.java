// repository/UserTypeRepository.java
package com.elmenus_lite.repository;

import com.jelmenus_lite.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Integer> {
}