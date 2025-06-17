package spring.practice.elmenus_lite.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record CartItemUpdateRequest(
        @PositiveOrZero
        Integer quantity
) {
}
