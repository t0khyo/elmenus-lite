package spring.practice.elmenus_lite.dto.response;
import jakarta.validation.constraints.NotNull;

public record MenuResponse(

        @NotNull(message = "Menu ID cannot be null")
        Integer id,


        @NotNull(message = "Restaurant ID cannot be null")
        Integer restaurantId,

        String name
) {
}
