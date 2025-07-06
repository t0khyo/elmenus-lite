package spring.practice.elmenus_lite.dto;

import lombok.Builder;
import lombok.Data;
import spring.practice.elmenus_lite.model.enums.CancellationInitiator;
import spring.practice.elmenus_lite.model.enums.CancellationProcessingStatus;
import spring.practice.elmenus_lite.model.enums.CancellationReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CancellationResponse {
    private Long cancellationId;
    private Long orderId;
    private CancellationInitiator initiatedBy;
    private CancellationReason reason;
    private String reasonDetails;
    private BigDecimal refundAmount;
    private BigDecimal cancellationFee;
    private BigDecimal restaurantCompensation;
    private CancellationProcessingStatus processingStatus;
    private LocalDateTime cancelledAt;
    private LocalDateTime refundProcessedAt;
    private boolean refundPending;
}