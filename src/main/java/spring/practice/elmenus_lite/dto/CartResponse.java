package spring.practice.elmenus_lite.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Integer cartId,
        List<CartItemResponse> items,
        BigDecimal totalAmount
) {
    public CartResponse {
        // If items are not null, calculate the totalAmount by summing up item totals
        if (items != null) {
            totalAmount = items.stream()
                    .map(CartItemResponse::totalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }else{
            totalAmount = BigDecimal.ZERO;
        }
    }
}
