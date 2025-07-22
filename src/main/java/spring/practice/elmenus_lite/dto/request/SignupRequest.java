package spring.practice.elmenus_lite.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record SignupRequest(
        @Email
        String email,

        @Length(min = 8, max = 255)
        String password,

//        @NotBlank
        @Length(max = 50)
        String firstName,

//        @NotBlank
        @Length(max = 50)
        String lastName
) {
}
