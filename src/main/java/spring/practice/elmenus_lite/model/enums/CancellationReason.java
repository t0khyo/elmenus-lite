package spring.practice.elmenus_lite.model.enums;


public enum CancellationReason {
    // Customer-initiated reasons
    CUSTOMER_CHANGED_MIND("Customer changed mind"),
    CUSTOMER_EMERGENCY("Customer emergency"),
    CUSTOMER_DUPLICATE_ORDER("Duplicate order"),
    CUSTOMER_WRONG_ADDRESS("Wrong delivery address"),
    CUSTOMER_PAYMENT_ISSUE("Customer payment issue"),

    // Restaurant-initiated reasons
    RESTAURANT_UNAVAILABLE("Restaurant unavailable"),
    RESTAURANT_ITEM_UNAVAILABLE("Menu items unavailable"),
    RESTAURANT_KITCHEN_ISSUE("Kitchen equipment issue"),
    RESTAURANT_TOO_BUSY("Restaurant too busy"),

    // System-initiated reasons
    SYSTEM_PAYMENT_FAILED("Payment processing failed"),
    SYSTEM_FRAUD_DETECTED("Fraud detection triggered"),
    SYSTEM_TECHNICAL_ERROR("Technical system error"),

    // Delivery-related reasons
    DELIVERY_UNAVAILABLE("No delivery personnel available"),
    DELIVERY_WEATHER("Weather conditions"),
    DELIVERY_ADDRESS_ISSUE("Delivery address issue"),

    // Admin reasons
    ADMIN_POLICY_VIOLATION("Policy violation"),
    ADMIN_CUSTOMER_REQUEST("Customer service request"),
    ADMIN_INVESTIGATION("Under investigation");

    private final String displayName;

    CancellationReason(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Business logic methods
    public boolean allowsFullRefund() {
        return switch (this) {
            case RESTAURANT_UNAVAILABLE, RESTAURANT_ITEM_UNAVAILABLE,
                 RESTAURANT_KITCHEN_ISSUE, SYSTEM_PAYMENT_FAILED,
                 SYSTEM_TECHNICAL_ERROR, DELIVERY_UNAVAILABLE -> true;
            default -> false;
        };
    }

    public boolean requiresRestaurantCompensation() {
        return switch (this) {
            case CUSTOMER_CHANGED_MIND, CUSTOMER_DUPLICATE_ORDER -> true;
            default -> false;
        };
    }
}