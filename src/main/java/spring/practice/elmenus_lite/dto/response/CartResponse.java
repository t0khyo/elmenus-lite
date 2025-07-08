package spring.practice.elmenus_lite.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Integer id,
        List<CartItemResponse> items,
        BigDecimal totalAmount
) {
}
