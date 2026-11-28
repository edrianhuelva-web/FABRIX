package fabrix;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class InventoryService {
    public static final String ALL_CATEGORIES = "All categories";
    public static final String[] LEVELS = {"All stock levels", "In Stock", "Low Stock", "Out of Stock"};

    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final List<Product> products;
    private final List<Order> orders;
    private final Cart cart = new Cart();

    public InventoryService() throws IOException {
        Path dir = Paths.get("data");
        productRepo = new ProductRepository(dir.resolve("products.csv"));
        orderRepo = new OrderRepository(dir.resolve("orders.csv"));
        products = productRepo.loadAll();
        orders = orderRepo.loadAll();
    }

    public List<Product> getProducts() { return products; }
    public List<Order> getOrders() { return orders; }
    public Cart getCart() { return cart; }
    
    public List<Product> search(String query, String category, int levelIndex) {
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Product> out = new ArrayList<>();
        for (Product p : products) {
            if (!q.isEmpty() && !(p.getName().toLowerCase().contains(q)
                    || p.getSku().toLowerCase().contains(q)
                    || p.getCategory().toLowerCase().contains(q)
                    || p.getCollection().toLowerCase().contains(q)
                    || p.getSize().toLowerCase().equals(q))) continue;
            if (category != null && !category.equals(ALL_CATEGORIES) && !p.getCategory().equalsIgnoreCase(category)) continue;
            if (levelIndex > 0 && p.getLevel().ordinal() != levelIndex - 1) continue;
            out.add(p);
        }
        return out;
    }

    public List<String> getCategories() {
        TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Product p : products) set.add(p.getCategory());
        return new ArrayList<>(set);
    }
    public int totalUnits() {
        int n = 0;
        for (Product p : products) n += p.getStock();
        return n;
    }
    public int lowStockAlerts() {
        int n = 0;
        for (Product p : products) if (p.getLevel() != Product.Level.IN_STOCK) n++;
        return n;
    }
    public int pendingOrders() {
        int n = 0;
        for (Order o : orders) if (o.getStatus() == OrderStatus.PENDING) n++;
        return n;
    }
    public List<Product> lowStockItems(int max) {
        List<Product> low = new ArrayList<>();
        for (Product p : products) if (p.getLevel() != Product.Level.IN_STOCK) low.add(p);
        low.sort(Comparator.comparingInt(Product::getStock));
        return low.size() > max ? new ArrayList<>(low.subList(0, max)) : low;
    }
    public List<Order> recentOrders(int max) {
        List<Order> all = new ArrayList<>(orders);
        Collections.reverse(all);
        return all.size() > max ? new ArrayList<>(all.subList(0, max)) : all;
    }
    public Product addProduct(String name, String category, String collection, String size,
                              String priceText, String stockText) throws IOException {
        name = name == null ? "" : name.trim();
        category = category == null ? "" : category.trim();
        collection = collection == null ? "" : collection.trim();
        size = size == null ? "" : size.trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Please enter the clothing name.");
        if (category.isEmpty()) throw new IllegalArgumentException("Please enter or choose a category.");
        if (size.isEmpty()) throw new IllegalArgumentException("Please choose a size.");
        double price;
        int stock;
        try {
            price = Double.parseDouble(priceText.replace(",", "").replace("\u20B1", "").trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Price must be a number, e.g. 1490.");
        }
        try {
            stock = Integer.parseInt(stockText.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Stock must be a whole number, e.g. 50.");
        }
        if (price < 0) throw new IllegalArgumentException("Price can't be negative.");
        if (stock < 0) throw new IllegalArgumentException("Stock can't be negative.");

        Product p = new Product(nextSku(), name, category, collection.isEmpty() ? "-" : collection, size, price, stock);
        products.add(p);
        productRepo.saveAll(products);
        return p;
    }
    
    public void updateProduct(Product p,
            String name,
            String category,
            String collection,
            String size,
            String priceText,
            String stockText) throws IOException {

        if (p == null) {
            throw new IllegalArgumentException("No product selected.");
        }

        name = name == null ? "" : name.trim();
        category = category == null ? "" : category.trim();
        collection = collection == null ? "" : collection.trim();
        size = size == null ? "" : size.trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Enter a clothing name.");
        }

        if (category.isEmpty()) {
            throw new IllegalArgumentException("Enter a category.");
        }

        if (size.isEmpty()) {
            throw new IllegalArgumentException("Enter a size.");
        }

        double price;
        int stock;

        try {
            price = Double.parseDouble(
                    priceText.replace(",", "")
                            .replace("\u20B1", "")
                            .trim()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Enter a valid price.");
        }

        try {
            stock = Integer.parseInt(stockText.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Enter a valid stock quantity.");
        }

        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }

        // Update the selected product
        p.setName(name);
        p.setCategory(category);
        p.setCollection(
                collection.isEmpty() ? "-" : collection
        );
        p.setSize(size);
        p.setPrice(price);
        p.setStock(stock);

        // Save changes to CSV
        productRepo.saveAll(products);
    }

    public void removeProduct(Product p) throws IOException {
        if (p == null) throw new IllegalArgumentException("Select a product to remove.");
        products.remove(p);
        productRepo.saveAll(products);
    }
    public void addToCart(Product p, int qty) { cart.add(p, qty); }

    public void removeFromCart(int row) { cart.remove(row); }

    public Order placeOrder(String supplier) throws IOException {
        if (cart.isEmpty()) throw new IllegalStateException("Your cart is empty.");
        if (supplier == null || supplier.isBlank()) throw new IllegalArgumentException("Please enter a supplier.");
        Order o = new Order(nextOrderId(), supplier.trim(), LocalDateTime.now().withNano(0),
                OrderStatus.PENDING, new ArrayList<>(cart.getItems()));
        orders.add(o);
        cart.clear();
        orderRepo.saveAll(orders);
        return o;
    }
    public void advanceOrder(Order o) throws IOException {
        if (o == null) throw new IllegalArgumentException("Select an order first.");
        if (o.getStatus().isFinal()) throw new IllegalStateException("This order is already delivered.");
        o.setStatus(o.getStatus().next());
        if (o.getStatus().isFinal()) {
            for (CartItem ci : o.getItems()) {
                for (Product p : products) {
                    if (p.getSku().equals(ci.getSku())) p.setStock(p.getStock() + ci.getQuantity());
                }
            }
            productRepo.saveAll(products);
        }
        orderRepo.saveAll(orders);
    }
    private String nextSku() {
        int n = products.size() + 1;
        while (true) {
            String sku = String.format("FX-%03d", n);
            boolean used = false;
            for (Product p : products) if (p.getSku().equals(sku)) { used = true; break; }
            if (!used) return sku;
            n++;
        }
    }

    private String nextOrderId() {
        int max = 1000;
        for (Order o : orders) {
            try { max = Math.max(max, Integer.parseInt(o.getId().substring(4))); }
            catch (Exception ignored) {}
        }
        return "FRX-" + (max + 1);
    }
}
