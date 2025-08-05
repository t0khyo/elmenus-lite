package spring.practice.elmenus_lite.dto;

import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "Address Id is required")
        Integer addressId, // Must reference existing address

        @NotNull(message = "Preferred Payment Setting Id is required")
        Integer preferredPaymentSettingId,  // can only use their saved payment methods

        String promotionCode
) {
}