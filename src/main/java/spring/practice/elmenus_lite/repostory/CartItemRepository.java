package spring.practice.elmenus_lite.repostory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.CartItem;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    boolean existsByIdAndCartId(Integer cartItemId, Integer cartId);

    List<CartItem> findByCartId(Integer cartId);

    void deleteByCartId(Integer cartId);
}
