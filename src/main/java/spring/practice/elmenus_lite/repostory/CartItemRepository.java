package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}