package spring.practice.elmenus_lite.dto;

import java.math.BigDecimal;

public record MenuItemResponse(
        Integer id,
        Integer menuId,
        String name,
        BigDecimal price
) {
}
