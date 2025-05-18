// repository/CategoryRepository.java
package com.elmenus_lite.repository;

import com.elmenus_lite.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
