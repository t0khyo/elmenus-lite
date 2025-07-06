package spring.practice.elmenus_lite.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.InvalidOrderException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.Cart;
import spring.practice.elmenus_lite.model.CartItem;
import spring.practice.elmenus_lite.model.MenuItem;
import spring.practice.elmenus_lite.model.enums.TransactionStatusEnum;
import spring.practice.elmenus_lite.repostory.CartItemRepository;
import spring.practice.elmenus_lite.repostory.CartRepository;
import spring.practice.elmenus_lite.repostory.MenuItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartUtils {
    private final CartRepository cartRepository;
    private final OwnershipValidator ownershipValidator;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;

    public Cart fetchCartByCustomerId(Integer customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND_FOR_CUSTOMER.getFinalMessage(customerId)));
    }

    public Cart fetchAndValidateCart(Integer cartId, Integer customerId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND.getFinalMessage(cartId)));

        ownershipValidator.validateCartOwnership(cart, customerId);
        return cart;
    }

    public CartItem fetchAndValidateCartItem(Integer cartItemId, Integer cartId) {
        validateCartItemBelongsToCart(cartItemId, cartId);
        return cartItemRepository.findById(cartItemId).get();
    }

    public List<CartItem> validateCartItems(Integer cartId, Integer restaurantId) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);

        if (cartItems.isEmpty()) {
            throw new InvalidOrderException(ErrorMessage.CART_EMPTY.getFinalMessage());
        }

        // Validate all cart items belong to the specified restaurant and are available
        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItem().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.MENU_ITEM_NOT_FOUND.getFinalMessage(cartItem.getMenuItem().getId())));

            // Check if menu item belongs to the specified restaurant
            if (!menuItem.getMenu().getRestaurant().getId().equals(restaurantId)) {
                throw new InvalidOrderException(
                        ErrorMessage.MENU_ITEM_WRONG_RESTAURANT.getFinalMessage(menuItem.getName())
                );
            }

            // Check if menu item is available
            if (!menuItem.getAvailable()) {
                throw new InvalidOrderException(ErrorMessage.MENU_ITEM_UNAVAILABLE.getFinalMessage(menuItem.getName()));
            }
        }

        return cartItems;
    }

    public void cleanupCart(Integer cartId, TransactionStatusEnum transactionStatus) {
        if (transactionStatus == TransactionStatusEnum.SUCCESS)
            cartItemRepository.deleteByCartId(cartId);
    }

    public void validateCartItemBelongsToCart(int cartItemId, int cartId) {
        if(!cartItemRepository.existsByIdAndCartId(cartItemId, cartId)) {
            throw new EntityNotFoundException(ErrorMessage.CART_ITEM_DOES_NOT_BELONG_TO_CART.getFinalMessage(cartItemId, cartId));
        }
    }

}
