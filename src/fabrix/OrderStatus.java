package fabrix;

public enum OrderStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered");

    private final String label;

    OrderStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
    public boolean isFinal() { return this == DELIVERED; }
    public OrderStatus next() { return isFinal() ? this : values()[ordinal() + 1]; }
}
