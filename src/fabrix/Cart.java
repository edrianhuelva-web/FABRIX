package fabrix;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() { return items; }

    public void add(Product p, int qty) {
        if (p == null) throw new IllegalArgumentException("Select a product first.");
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be at least 1.");
        for (CartItem i : items) {
            if (i.getSku().equals(p.getSku())) {
                if (i.getQuantity() + qty > 9999) throw new IllegalArgumentException("Maximum 9,999 per item.");
                i.setQuantity(i.getQuantity() + qty);
                return;
            }
        }
        if (qty > 9999) throw new IllegalArgumentException("Maximum 9,999 per item.");
        items.add(new CartItem(p.getSku(), p.getName(), p.getSize(), qty, p.getPrice()));
    }

    public void remove(int index) {
        if (index < 0 || index >= items.size()) throw new IllegalArgumentException("Select a cart item to remove.");
        items.remove(index);
    }

    public void clear() { items.clear(); }
    public boolean isEmpty() { return items.isEmpty(); }

    public double getTotal() {
        double t = 0;
        for (CartItem i : items) t += i.getSubtotal();
        return t;
    }
}
