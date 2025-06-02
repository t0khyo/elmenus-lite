package spring.practice.elmenus_lite.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetails(
        Integer id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime orderDate,
        String status,
        List<OrderItemDTO> items,
        PaymentDetails paymentDetails,
        DeliveryAddress deliveryAddress, // Using a specific DTO for delivery address, could reuse a generic AddressDto
        TrackingInfo trackingInfo,
        String promotion
) {
}
