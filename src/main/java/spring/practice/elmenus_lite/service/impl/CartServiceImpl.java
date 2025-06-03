package spring.practice.elmenus_lite.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.CartItemRequest;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final CartMapper cartMapper;

    @Override
    public CartResponse getCartByCustomerId(Integer customerId) {
        return cartMapper.toCartResponse(getCartEntityByCustomerId(customerId));
    }

    @Override
    public CartResponse addItemToCart(Integer customerId, CartItemRequest cartItemRequest) {
        Customer customer = getCustomerById(customerId);
        Cart cart = Optional.ofNullable(customer.getCart())
                .orElse(new Cart().setCustomer(customer));

        // check if the requested item exits in the cart already
        CartItem cartExistingItem = cart.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(cartItemRequest.menuItemId()))
                .findFirst()
                .orElse(null);


        if (cartExistingItem != null) {
            cartExistingItem.setQuantity(cartExistingItem.getQuantity() + cartItemRequest.quantity());

        } else {
            MenuItem menuItem = getMenuItemById(cartItemRequest.menuItemId());

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
        if (!isCartItemBelongsToCart(cartId, cartItemId))
            throw new RuntimeException("CartItem not found");

        CartItem cartItem = getCartItemById(cartItemId);
        cartItemRepository.delete(cartItem);

        Cart cart = getCartById(cartId);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse clearCart(Integer cartId) {
        Cart cart = getCartById(cartId);
        cartItemRepository.deleteAllInBatch(cart.getItems());
        cart.getItems().clear();

        return cartMapper.toCartResponse(cart);
    }

    // helper methods
    private Cart getCartById(int id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + id));
    }

    private CartItem getCartItemById(int id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart Item not found with id: " + id));
    }

    private Boolean isCartItemBelongsToCart(int cartId, int cartItemId) {
        return cartItemRepository.existsByIdAndCartId(cartItemId, cartId);
    }

    private Cart getCartEntityByCustomerId(int customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart Not found with customer id: " + customerId));
    }

    private Customer getCustomerById(int customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
    }

    private MenuItem getMenuItemById(int menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found with id: " + menuItemId));
    }
}
