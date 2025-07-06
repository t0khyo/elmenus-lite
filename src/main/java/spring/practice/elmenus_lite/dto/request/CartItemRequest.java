package spring.practice.elmenus_lite.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CartItemRequest(
        @NotNull(message = "Menu Item Id is required")
        Integer menuItemId,

        @NotNull(message = "Quantity is required")
        @PositiveOrZero
        Integer quantity
) {
}
