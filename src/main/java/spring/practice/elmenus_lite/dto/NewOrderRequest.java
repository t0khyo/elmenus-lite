package spring.practice.elmenus_lite.dto;

import jakarta.validation.constraints.NotNull;

public record NewOrderRequest(
        @NotNull(message = "Customer Id is required")
        Integer customerId,
        @NotNull(message = "Cart Id is required")
        Integer cartId,
        @NotNull(message = "Restaurant Id is required")
        Integer restaurantId,
        @NotNull(message = "Address Id is required")
        Integer addressId, // Must reference existing address
        @NotNull(message = "preferredPaymentSetting Id is required")
        Integer preferredPaymentSettingId,  // can only use their saved payment methods
        String promotionCode
) {}