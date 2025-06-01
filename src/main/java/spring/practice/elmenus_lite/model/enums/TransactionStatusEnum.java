package spring.practice.elmenus_lite.model.enums;

import lombok.Getter;

@Getter
public enum TransactionStatusEnum {
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    PENDING("PENDING");

    private final String status;

    TransactionStatusEnum(String status) {
        this.status = status;
    }

    public static TransactionStatusEnum fromString(String status) {
        for (TransactionStatusEnum paymentStatus : TransactionStatusEnum.values()) {
            if (paymentStatus.status.equalsIgnoreCase(status)) {
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + status);
    }
}
