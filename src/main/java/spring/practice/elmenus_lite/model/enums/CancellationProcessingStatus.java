package spring.practice.elmenus_lite.model.enums;

public enum CancellationProcessingStatus {
    PENDING("Cancellation pending"),
    REFUND_PROCESSING("Processing refund"),
    REFUND_COMPLETED("Refund completed"),
    FAILED("Cancellation failed"),
    COMPLETED("Cancellation completed");

    private final String displayName;

    CancellationProcessingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}