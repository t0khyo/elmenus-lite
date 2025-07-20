package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
}