package spring.practice.elmenus_lite.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

public record CartItemUpdateRequest(
        @PositiveOrZero
        Integer quantity
) {
}
