package spring.practice.elmenus_lite.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.model.Cart;
import spring.practice.elmenus_lite.repostory.CartRepository;

@Service
@RequiredArgsConstructor
public class CartUtils {
    private final CartRepository cartRepository;

    public Cart fetchCartByCustomerId(Integer customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND_FOR_CUSTOMER.getFinalMessage(customerId)));
    }

}
