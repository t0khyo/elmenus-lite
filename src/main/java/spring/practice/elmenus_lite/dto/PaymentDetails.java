package spring.practice.elmenus_lite.dto;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDetails(
        String method,
        UUID transactionId,
        BigDecimal amount,
        String status
) {
}
