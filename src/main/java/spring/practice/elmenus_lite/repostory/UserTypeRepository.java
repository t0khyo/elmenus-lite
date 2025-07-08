package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.UserType;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Integer> {
}