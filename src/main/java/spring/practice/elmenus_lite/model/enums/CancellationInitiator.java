package spring.practice.elmenus_lite.model.enums;


public enum CancellationInitiator {
    CUSTOMER("Customer"),
    RESTAURANT("Restaurant"),
    SYSTEM("System"),
    ADMIN("Administrator"),
    DELIVERY_PARTNER("Delivery Partner");

    private final String displayName;

    CancellationInitiator(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
