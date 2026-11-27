package fabrix;

public class Product {

    public enum Level {
        IN_STOCK("In Stock"), LOW_STOCK("Low Stock"), OUT_OF_STOCK("Out of Stock");
        private final String label;
        Level(String label) { this.label = label; }
        public String getLabel() { return label; }
    }
    public static final int LOW_STOCK_LIMIT = 10;

    private final String sku;
    private final String name;
    private final String category;
    private final String collection;
    private final String size;
    private final double price;
    private int stock;

    public Product(String sku, String name, String category, String collection, String size, double price, int stock) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.collection = collection;
        this.size = size;
        this.price = price;
        this.stock = stock;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getCollection() { return collection; }
    public String getSize() { return size; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Level getLevel() {
        if (stock <= 0) return Level.OUT_OF_STOCK;
        if (stock <= LOW_STOCK_LIMIT) return Level.LOW_STOCK;
        return Level.IN_STOCK;
    }
}
