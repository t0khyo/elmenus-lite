package spring.practice.elmenus_lite.dto;

import spring.practice.elmenus_lite.model.enums.PaymentStatusEnum;

public record PaymentResult(
        PaymentStatusEnum status,    // PENDING, FAILED , SUCCESS
        String paymentMethod,        // Credit Card, Cash, etc.
        String transactionId
) {}