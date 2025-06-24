package spring.practice.elmenus_lite.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.InvalidOrderException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.repostory.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderUtils {
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final PreferredPaymentSettingRepository preferredPaymentSettingRepository;
    private final MenuItemRepository menuItemRepository;
    private final PromotionRepository promotionRepository;
    private final OwnershipValidator ownershipValidator;

    public BigDecimal calculateDiscountAmount(BigDecimal subtotal, Promotion promotion) {
        if (promotion == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountPercent = promotion.getDiscountPercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal calculatedDiscount = subtotal.multiply(discountPercent);
        return calculatedDiscount.min(promotion.getMaxDiscount());
    }

    public BigDecimal calculateSubtotal(List<CartItem> cartItems) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItem().getId()).get(); //Checked before
            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        return subtotal;
    }

//    public void validateOwnership(Integer entityOwnerId, Integer customerId, String entityType) {
//        if (!entityOwnerId.equals(customerId)) {
//            throw new InvalidOrderException(entityType + " does not belong to the specified customer");
//        }
//    }

    public Cart fetchAndValidateCart(Integer cartId, Integer customerId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        ownershipValidator.validateCartOwnership(cart, customerId);
        return cart;
    }

    public Restaurant fetchAndValidateRestaurant(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getFinalMessage(restaurantId)));

        if (!restaurant.isActive()) {
            throw new InvalidOrderException(ErrorMessage.RESTAURANT_INACTIVE.getFinalMessage());
        }
        return restaurant;
    }

    public Address fetchAndValidateAddress(Integer addressId, Integer customerId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        ownershipValidator.validateAddressOwnership(address, customerId);
        return address;
    }

    public PreferredPaymentSetting fetchAndValidatePaymentSetting(Integer paymentSettingId, Integer customerId) {
        PreferredPaymentSetting paymentSetting = preferredPaymentSettingRepository.findById(paymentSettingId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PAYMENT_SETTING_NOT_FOUND.getFinalMessage(paymentSettingId)));

        ownershipValidator.validatePaymentSettingOwnership(paymentSetting, customerId);
        return paymentSetting;
    }

    public Promotion fetchAndValidatePromotion(String promotionCode) {
        if (promotionCode == null || promotionCode.trim().isEmpty()) {
            return null;
        }

        Promotion promotion = promotionRepository.findByCode(promotionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with code: " + promotionCode));

        // Validate promotion is active and within date range
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartAt()) || now.isAfter(promotion.getEndAt())) {
            throw new InvalidOrderException("Promotion code is not valid or has expired");
        }
        return promotion;
    }


    public List<CartItem> validateCartItems(Integer cartId, Integer restaurantId) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);

        if (cartItems.isEmpty()) {
            throw new InvalidOrderException("Cart is empty");
        }

        // Validate all cart items belong to the specified restaurant and are available
        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItem().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + cartItem.getMenuItem().getId()));

            // Check if menu item belongs to the specified restaurant
            if (!menuItem.getMenu().getRestaurant().getId().equals(restaurantId)) {
                throw new InvalidOrderException("Menu item " + menuItem.getName() + " does not belong to the specified restaurant");
            }

            // Check if menu item is available
            if (!menuItem.getAvailable()) {
                throw new InvalidOrderException("Menu item " + menuItem.getName() + " is currently unavailable");
            }
        }

        return cartItems;
    }
}
