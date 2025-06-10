package spring.practice.elmenus_lite.model.enums;


import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    PENDING("PENDING"),                 // When the order is first placed, awaiting confirmation.
    CONFIRMED("CONFIRMED"),             // After initial validation/payment, order is accepted by the restaurant.
    PREPARING("PREPARING"),             // Restaurant is currently preparing the order.
    OUT_FOR_DELIVERY("OUT_FOR_DELIVERY"), // Order has left the restaurant and is on the way to the customer.
    DELIVERED("DELIVERED"),             // Order has been successfully delivered to the customer.
    CANCELLED("CANCELLED"),             // Order was cancelled by the customer or restaurant or system before delivery.
    FAILED("FAILED");                   // Payment failed or an unexpected issue occurred during order processing.


    private final String status;

    OrderStatusEnum(String status) {
        this.status = status;
    }

    public static OrderStatusEnum fromString(String status) {
        for (OrderStatusEnum s : OrderStatusEnum.values()) {
            if (s.getStatus().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + status);
    }
}
