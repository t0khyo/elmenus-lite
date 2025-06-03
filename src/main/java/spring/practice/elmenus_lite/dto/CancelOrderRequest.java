package spring.practice.elmenus_lite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import spring.practice.elmenus_lite.model.enums.CancellationReason;

@Data
@Builder
public class CancelOrderRequest {

    @NotNull(message = "Cancellation reason is required")
    private CancellationReason reason;

    @Size(max = 1000, message = "Reason details cannot exceed 1000 characters")
    private String reasonDetails;

    @NotNull(message = "Requested by is required")
    private String requestedBy;
}

