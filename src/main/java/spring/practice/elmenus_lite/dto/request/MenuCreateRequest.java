package spring.practice.elmenus_lite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MenuCreateRequest(
        @Size(max = 100, message = "Menu name cannot exceed 100 characters")
        @NotBlank(message = "Menu name cannot be empty") // Name is required for creation
        String name
) {
}
