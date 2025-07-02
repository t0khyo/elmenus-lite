package spring.practice.elmenus_lite.dto.request;

import jakarta.validation.constraints.Size;

public record MenuUpdateRequest(
        @Size(max = 100, message = "Menu name cannot exceed 100 characters")
         String name
) {
}
