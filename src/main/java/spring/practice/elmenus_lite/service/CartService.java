package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.request.CartItemRequest;
import spring.practice.elmenus_lite.dto.request.CartItemUpdateRequest;
import spring.practice.elmenus_lite.dto.response.CartResponse;

public interface CartService {
    CartResponse removeCartItem(Integer cartId, Integer cartItemId);

    CartResponse clearCart(Integer cartId);

    CartResponse getCartByCustomerId(Integer customerId);

    CartResponse addItemToCart(Integer customerId, CartItemRequest cartItemRequest);

    CartResponse updateCartItem(Integer cartId, Integer cartItemId, CartItemUpdateRequest cartItemUpdateRequest);
}
