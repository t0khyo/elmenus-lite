package spring.practice.elmenus_lite.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


public record CartItemRequest(
    @NotNull(message = "Menu Item Id is required")
    Integer menuItemId,
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than or equal to 1")
    Integer quantity
) {}
