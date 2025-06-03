package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "order_cancellations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancellation_id")
    private Long id;

    // Link to the cancelled order
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    private Order order;

    // Who initiated the cancellation
    @Enumerated(EnumType.STRING)
    @Column(name = "initiated_by", nullable = false)
    @NotNull(message = "Initiator is required")
    private CancellationInitiator initiatedBy;

    // Why was it cancelled
    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_reason", nullable = false)
    @NotNull(message = "Cancellation reason is required")
    private CancellationReason cancellationReason;

    // Free text explanation
    @Column(name = "reason_details", length = 1000)
    @Size(max = 1000, message = "Reason details cannot exceed 1000 characters")
    private String reasonDetails;

    // Financial impact
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "restaurant_compensation", precision = 10, scale = 2)
    private BigDecimal restaurantCompensation;

    @Column(name = "cancellation_fee", precision = 10, scale = 2)
    private BigDecimal cancellationFee;

    // Processing status
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    @Builder.Default
    private CancellationProcessingStatus processingStatus = CancellationProcessingStatus.PENDING;

    // Timestamps
    @Column(name = "cancelled_at", nullable = false)
    @Builder.Default
    private LocalDateTime cancelledAt = LocalDateTime.now();

    @Column(name = "refund_processed_at")
    private LocalDateTime refundProcessedAt;

    @Column(name = "restaurant_notified_at")
    private LocalDateTime restaurantNotifiedAt;

    // System tracking
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    // Business methods
    public boolean isRefundPending() {
        return refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0
                && refundProcessedAt == null;
    }

    public boolean requiresRestaurantCompensation() {
        return restaurantCompensation != null && restaurantCompensation.compareTo(BigDecimal.ZERO) > 0;
    }
}
