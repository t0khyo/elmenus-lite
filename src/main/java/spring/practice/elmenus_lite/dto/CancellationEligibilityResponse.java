package spring.practice.elmenus_lite.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CancellationEligibilityResponse {
    private Long orderId;
    private boolean eligible;
    private String reason;
    private BigDecimal cancellationFee;
    private BigDecimal refundAmount;
    private LocalDateTime timeLimit;
}