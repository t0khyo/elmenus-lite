package spring.practice.elmenus_lite.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatusEnum {
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    PENDING("PENDING");

    private final String status;

    PaymentStatusEnum(String status) {
        this.status = status;
    }

    public static PaymentStatusEnum fromString(String status) {
        for (PaymentStatusEnum paymentStatus : PaymentStatusEnum.values()) {
            if (paymentStatus.status.equalsIgnoreCase(status)) {
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + status);
    }
}
