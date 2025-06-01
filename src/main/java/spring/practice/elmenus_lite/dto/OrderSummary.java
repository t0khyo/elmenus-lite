package spring.practice.elmenus_lite.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import spring.practice.elmenus_lite.model.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Builder
public record OrderSummary(
        Long orderId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime orderDate,
        // Using BigDecimal for monetary values to avoid precision issues.
        BigDecimal totalAmount,
        OrderStatusEnum status,
        String paymentMethod
)
{}