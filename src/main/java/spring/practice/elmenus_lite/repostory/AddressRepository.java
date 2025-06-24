package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.practice.elmenus_lite.model.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}