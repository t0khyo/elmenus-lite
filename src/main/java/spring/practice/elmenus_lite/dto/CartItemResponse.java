package spring.practice.elmenus_lite.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Integer cartItemId,
        MenuItemResponse menuItem,
        Integer quantity,
        BigDecimal totalPrice
) {}

