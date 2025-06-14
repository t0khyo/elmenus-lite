package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.CartItemRequest;
import spring.practice.elmenus_lite.dto.CartItemUpdateRequest;
import spring.practice.elmenus_lite.dto.CartResponse;

public interface CartService {
    CartResponse removeCartItem(Integer cartId, Integer cartItemId);

    CartResponse clearCart(Integer cartId);

    CartResponse getCartByCustomerId(Integer customerId);

    CartResponse addItemToCart(Integer customerId, CartItemRequest cartItemRequest);

    CartResponse updateCartItem(Integer cartId, Integer cartItemId, CartItemUpdateRequest cartItemUpdateRequest);
}
