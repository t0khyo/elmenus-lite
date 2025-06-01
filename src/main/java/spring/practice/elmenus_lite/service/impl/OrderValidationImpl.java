package spring.practice.elmenus_lite.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.exception.InvalidOrderException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.repostory.*;
import spring.practice.elmenus_lite.service.OrderValidation;
//import spring.practice.elmenus_lite.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderValidationImpl implements OrderValidation {
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final PreferredPaymentSettingRepository preferredPaymentSettingRepository;
    private final MenuItemRepository menuItemRepository;
    private final PromotionRepository promotionRepository;

    @Override
    public void validateOwnership(Integer entityOwnerId, Integer customerId, String entityType) {
        if (!entityOwnerId.equals(customerId)) {
            throw new InvalidOrderException(entityType + " does not belong to the specified customer");
        }
    }

    @Override
    public Customer fetchCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
    }

    @Override
    public Cart fetchAndValidateCart(Integer cartId, Integer customerId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        validateOwnership(cart.getCustomer().getId(), customerId, "Cart");
        return cart;
    }

    @Override
    public Restaurant fetchAndValidateRestaurant(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        if (!restaurant.getActive()) {
            throw new InvalidOrderException("Restaurant is currently inactive");
        }
        return restaurant;
    }

    @Override
    public Address fetchAndValidateAddress(Integer addressId, Integer customerId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        validateOwnership(address.getCustomer().getId(), customerId, "Address");
        return address;
    }

    @Override
    public PreferredPaymentSetting fetchAndValidatePaymentSetting(Integer paymentSettingId, Integer customerId) {
        PreferredPaymentSetting paymentSetting = preferredPaymentSettingRepository.findById(paymentSettingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment setting not found with ID: " + paymentSettingId));

        validateOwnership(paymentSetting.getCustomer().getId(), customerId, "Payment setting");
        return paymentSetting;
    }

    @Override
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


    @Override
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
