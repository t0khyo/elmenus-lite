package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.CartItemRequest;
import spring.practice.elmenus_lite.dto.CartItemResponse;
import spring.practice.elmenus_lite.dto.CartResponse;

public interface CartService {
    CartResponse removeCartItem(Integer cartId, Integer cartItemId);
    CartResponse clearCart(Integer cartId);
    CartResponse getCartByCustomerId(Integer customerId);
    CartItemResponse addItemToCart (Integer customerId, CartItemRequest cartItemRequest);
    CartItemResponse  updateCartItem( Integer cartId, Integer cartItemId,Integer Quantity);
}
