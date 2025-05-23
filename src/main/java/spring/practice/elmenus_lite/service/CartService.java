package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.CartResponse;

import java.util.Optional;

public interface CartService {
    CartResponse removeCartItem(Integer cartId, Integer cartItemId);

    CartResponse clearCart(Integer cartId);
    CartResponse getCartByCustomerId(Integer customerId);
}
