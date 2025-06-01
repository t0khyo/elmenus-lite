package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.PaymentResult;
import spring.practice.elmenus_lite.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderValidation {

    void validateOwnership(Integer entityOwnerId, Integer customerId, String entityType);

    Customer fetchCustomer(Integer customerId);

    Cart fetchAndValidateCart(Integer cartId, Integer customerId);

    Restaurant fetchAndValidateRestaurant(Integer restaurantId);

    Address fetchAndValidateAddress(Integer addressId, Integer customerId);

    PreferredPaymentSetting fetchAndValidatePaymentSetting(Integer paymentSettingId, Integer customerId);

    Promotion fetchAndValidatePromotion(String promotionCode) ;

    List<CartItem> validateCartItems(Integer cartId, Integer restaurantId);



}
