package spring.practice.elmenus_lite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO for standard error response
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private Integer status;
    private String error; // Short description of the error type (e.g., "Bad Request", "Not Found")
    private String message; // Detailed error message for developers or specific user feedback
    private String path; // The request URI path where the error occurred
}
