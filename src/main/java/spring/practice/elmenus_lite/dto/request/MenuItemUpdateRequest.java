package spring.practice.elmenus_lite.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemUpdateRequest(
        @Size(max = 50, message = "Menu item name cannot exceed 50 characters")
         String name,

        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        BigDecimal price
) {
}
