package fabrix;

public class CartItem {
    private final String sku;
    private final String name;
    private final String size;
    private int quantity;
    private final double unitPrice;

    public CartItem(String sku, String name, String size, int quantity, double unitPrice) {
        this.sku = sku;
        this.name = name;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return unitPrice * quantity; }
}
