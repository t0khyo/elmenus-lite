package spring.practice.elmenus_lite.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.CartItemRequest;
import spring.practice.elmenus_lite.dto.CartItemUpdateRequest;
import spring.practice.elmenus_lite.dto.CartResponse;
import spring.practice.elmenus_lite.mapper.CartMapper;
import spring.practice.elmenus_lite.model.Cart;
import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.model.Customer;
import spring.practice.elmenus_lite.model.MenuItem;
import spring.practice.elmenus_lite.repostory.CartItemRepository;
import spring.practice.elmenus_lite.repostory.CartRepository;
import spring.practice.elmenus_lite.repostory.CustomerRepository;
import spring.practice.elmenus_lite.repostory.MenuItemRepository;
import spring.practice.elmenus_lite.service.CartService;
import spring.practice.elmenus_lite.util.CartUtils;
import spring.practice.elmenus_lite.util.CustomerUtils;
import spring.practice.elmenus_lite.util.MenuUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final CartMapper cartMapper;
    private final CartUtils cartUtils;
    private final CustomerUtils customerUtils;
    private final MenuUtils menuUtils;

    @Override
    public CartResponse getCartByCustomerId(Integer customerId) {
        return cartMapper.toCartResponse(cartUtils.fetchCartByCustomerId(customerId));
    }

    @Override
    public CartResponse updateCartItem(Integer cartId, Integer cartItemId, CartItemUpdateRequest cartItemUpdateRequest) {
        CartItem cartItem = cartUtils.fetchAndValidateCartItem(cartItemId, cartId);

        if (cartItemUpdateRequest.quantity() == 0) {
            // Delete Cart Item if updated quantity = 0
            cartItemRepository.delete(cartItem);
        } else {
            // Update quantity
            cartItem.setQuantity(cartItemUpdateRequest.quantity());
            cartItemRepository.save(cartItem);
        }

        return cartMapper.toCartResponse(cartItem.getCart());
    }

    @Override
    public CartResponse addItemToCart(Integer customerId, CartItemRequest cartItemRequest) {
        Customer customer = customerUtils.fetchCustomer(customerId);
        Cart cart = Optional.ofNullable(customer.getCart())
                .orElse(new Cart().setCustomer(customer));

        // check if the requested item exits in the cart already
        CartItem cartExistingItem = cart.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(cartItemRequest.menuItemId()))
                .findFirst()
                .orElse(null);

        // if the item exists in the cart update the quantity
        // else create new cart item
        if (cartExistingItem != null) {
            cartExistingItem.setQuantity(cartExistingItem.getQuantity() + cartItemRequest.quantity());

        } else {
            MenuItem menuItem = menuUtils.fetchAndValidateMenuItem(cartItemRequest.menuItemId());

            CartItem newCartItem = new CartItem()
                    .setCart(cart)
                    .setMenuItem(menuItem)
                    .setQuantity(cartItemRequest.quantity());

            cart.addItem(newCartItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public CartResponse removeCartItem(Integer cartId, Integer cartItemId) {
        CartItem cartItem = cartUtils.fetchAndValidateCartItem(cartItemId, cartId);
        cartItemRepository.delete(cartItem);

        // todo add customer id using security context holder
        // Cart cart = cartUtils.fetchAndValidateCart(cartId, CustomerId);
        Cart cart = cartUtils.fetchAndValidateCart(cartId, 1);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse clearCart(Integer cartId) {
        // todo add customer id using security context holder
        Cart cart = cartUtils.fetchAndValidateCart(cartId, 1);
        cartItemRepository.deleteAllInBatch(cart.getItems());
        cart.getItems().clear();

        return cartMapper.toCartResponse(cart);
    }
}
