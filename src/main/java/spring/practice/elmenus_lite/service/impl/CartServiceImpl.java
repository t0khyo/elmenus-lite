package spring.practice.elmenus_lite.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.request.CartItemRequest;
import spring.practice.elmenus_lite.dto.request.CartItemUpdateRequest;
import spring.practice.elmenus_lite.dto.response.CartResponse;
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
    public CartResponse updateCartItem(Integer cartId, Integer cartItemId, CartItemUpdateRequest cartItemUpdateRequest) {
        if (!isCartItemBelongsToCart(cartId, cartItemId))
            throw new EntityNotFoundException("CartItem not found with id: " + cartItemId);

        CartItem cartItem = getCartItemById(cartItemId);

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
        Customer customer = getCustomerById(customerId);
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
            throw new EntityNotFoundException("CartItem not found with id: " + cartItemId);

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
    private Cart getCartById(int cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));
    }

    private CartItem getCartItemById(int cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart Item not found with id: " + cartItemId));
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
