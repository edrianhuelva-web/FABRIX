package fabrix;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private final String id;
    private final String supplier;
    private final LocalDateTime placedAt;
    private OrderStatus status;
    private final List<CartItem> items;

    public Order(String id, String supplier, LocalDateTime placedAt, OrderStatus status, List<CartItem> items) {
        this.id = id;
        this.supplier = supplier;
        this.placedAt = placedAt;
        this.status = status;
        this.items = items;
    }

    public String getId() { return id; }
    public String getSupplier() { return supplier; }
    public LocalDateTime getPlacedAt() { return placedAt; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<CartItem> getItems() { return items; }

    public int getUnits() {
        int u = 0;
        for (CartItem i : items) u += i.getQuantity();
        return u;
    }

    public double getTotal() {
        double t = 0;
        for (CartItem i : items) t += i.getSubtotal();
        return t;
    }
}
