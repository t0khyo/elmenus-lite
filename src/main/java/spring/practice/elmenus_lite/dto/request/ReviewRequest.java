package spring.practice.elmenus_lite.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Byte rating;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment; // Optional

}
