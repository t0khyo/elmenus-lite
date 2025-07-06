package spring.practice.elmenus_lite.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        String name,
        int quantity,
        BigDecimal unitPrice
) {
}
