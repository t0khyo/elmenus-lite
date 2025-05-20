package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.CartResponse;

public interface CartService {
    CartResponse removeCartItem(Integer cartId, Integer cartItemId);

    CartResponse clearCart(Integer cartId);
}
