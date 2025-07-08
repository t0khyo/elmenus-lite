package spring.practice.elmenus_lite.dto;

import spring.practice.elmenus_lite.model.enums.TransactionStatusEnum;

public record PaymentResult(
        TransactionStatusEnum status,    // PENDING, FAILED , SUCCESS
        String paymentMethod,        // Credit Card, Cash, etc.
        String transactionId
) {}