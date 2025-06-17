package spring.practice.elmenus_lite.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderSummaryResponse(
        Integer id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        String status,
        String paymentType
) {
}