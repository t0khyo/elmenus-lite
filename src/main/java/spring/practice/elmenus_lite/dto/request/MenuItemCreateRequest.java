package spring.practice.elmenus_lite.dto.request;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record MenuItemCreateRequest(
        @NotBlank(message = "Menu item name cannot be empty")
        @Size(max = 50, message = "Menu item name cannot exceed 50 characters")
        String name,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        BigDecimal price
) {
}
