package spring.practice.elmenus_lite.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record SignUpRequest(
        @Email
        String email,

        @Length(max = 255, min = 8)
        String password,

        @NotBlank
        @Length(max = 50)
        String firstName,

        @NotBlank
        @Length(max = 50)
        String lastName
) {
}
