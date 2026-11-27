package fabrix;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Order Clothes: pick items, Add to Cart, Remove from Cart, Place Order. */
public class OrderPage extends Page {
    private final JTextField search = Theme.field("Search clothes...");
    private final DefaultTableModel productModel = Theme.model("Item", "Size", "In stock", "Unit cost");
    private final JTable productTable = Theme.table(productModel, 52, 0);
    private final JTextField qty = Theme.field(null);

    private final DefaultTableModel cartModel = Theme.model("Item", "Size", "Qty", "Subtotal");
    private final JTable cartTable = Theme.table(cartModel, 44, 0);
    private final JLabel total = Theme.label("\u20B10.00", Theme.F_H2, Theme.TEXT);
    private final JComboBox<String> supplier = new JComboBox<>(new String[]{
            "Maison Textiles", "Atelier Supply", "Studio Linen"});
    private final JLabel msg = Theme.label(" ", Theme.F_BASE, Theme.MUTED);

    private List<Product> shown = new ArrayList<>();

    public OrderPage(InventoryService svc, DashboardFrame app) {
        super(svc, app);
        add(Theme.header("Order Clothes", "Restock from your suppliers. Build a cart, then place the order.", null), BorderLayout.NORTH);

        // ----- left: pick clothes -----
        productTable.getColumnModel().getColumn(0).setCellRenderer(new Theme.ProductCell(0));
        productTable.getColumnModel().getColumn(0).setPreferredWidth(190);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(45);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(65);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(105);
        qty.setText("10");
        Theme.size(qty, 80, 40);
        Theme.Btn addBtn = Theme.btn("Add to Cart", Theme.Kind.PRIMARY);
        addBtn.addActionListener(e -> onAdd());
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
        leftTop.add(Theme.label("Select clothes", Theme.F_H2, Theme.TEXT));
        leftTop.add(Box.createVerticalStrut(4));
        leftTop.add(Theme.label("Pick an item, set a quantity, add it to your cart.", Theme.F_BASE, Theme.MUTED));
        leftTop.add(Box.createVerticalStrut(14));
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        searchWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchWrap.add(search);
        leftTop.add(searchWrap);
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        leftBottom.setOpaque(false);
        leftBottom.add(Theme.label("Quantity", Theme.F_BASE, Theme.MUTED));
        leftBottom.add(qty);
        leftBottom.add(addBtn);
        Theme.Card left = new Theme.Card(new BorderLayout(0, 14));
        left.add(leftTop, BorderLayout.NORTH);
        left.add(Theme.scroll(productTable, false), BorderLayout.CENTER);
        left.add(leftBottom, BorderLayout.SOUTH);

        // ----- right: cart -----
        supplier.setEditable(true);
        Theme.styleCombo(supplier);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        Theme.Btn removeBtn = Theme.btn("Remove from Cart", Theme.Kind.DANGER);
        removeBtn.addActionListener(e -> onRemove());
        Theme.Btn placeBtn = Theme.btn("Place Order", Theme.Kind.PRIMARY);
        placeBtn.addActionListener(e -> onPlace());

        JPanel rightTop = new JPanel();
        rightTop.setOpaque(false);
        rightTop.setLayout(new BoxLayout(rightTop, BoxLayout.Y_AXIS));
        rightTop.add(Theme.label("Order cart", Theme.F_H2, Theme.TEXT));
        rightTop.add(Box.createVerticalStrut(4));
        rightTop.add(Theme.label("Review your items before placing the order.", Theme.F_BASE, Theme.MUTED));
        rightTop.add(Box.createVerticalStrut(14));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalRow.add(Theme.label("Order total", Theme.F_BASE, Theme.MUTED), BorderLayout.WEST);
        totalRow.add(total, BorderLayout.EAST);
        JPanel supplierWrap = new JPanel(new BorderLayout());
        supplierWrap.setOpaque(false);
        supplierWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        supplierWrap.add(supplier);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(removeBtn);
        buttons.add(placeBtn);
        JPanel rightBottom = new JPanel();
        rightBottom.setOpaque(false);
        rightBottom.setLayout(new BoxLayout(rightBottom, BoxLayout.Y_AXIS));
        rightBottom.add(totalRow);
        rightBottom.add(Box.createVerticalStrut(14));
        rightBottom.add(Theme.label("Supplier", Theme.F_SMALL, Theme.MUTED));
        rightBottom.add(Box.createVerticalStrut(4));
        rightBottom.add(supplierWrap);
        rightBottom.add(Box.createVerticalStrut(14));
        rightBottom.add(buttons);
        rightBottom.add(Box.createVerticalStrut(8));
        rightBottom.add(msg);

        Theme.Card right = new Theme.Card(new BorderLayout(0, 14));
        right.add(rightTop, BorderLayout.NORTH);
        right.add(Theme.scroll(cartTable, false), BorderLayout.CENTER);
        right.add(rightBottom, BorderLayout.SOUTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 24, 0));
        body.setOpaque(false);
        body.add(left);
        body.add(right);
        add(body, BorderLayout.CENTER);

        search.getDocument().addDocumentListener(Theme.onChange(this::refreshProducts));
    }

    private void onAdd() {
        int row = productTable.getSelectedRow();
        if (row < 0) { Theme.status(msg, "Select an item from the list first.", false); return; }
        try {
            int q = Integer.parseInt(qty.getText().trim());
            Product p = shown.get(row);
            svc.addToCart(p, q);
            refreshCart();
            Theme.status(msg, "Added " + q + " x " + p.getName() + " (" + p.getSize() + ") to the cart.", true);
        } catch (NumberFormatException ex) {
            Theme.status(msg, "Quantity must be a whole number.", false);
        } catch (RuntimeException ex) {
            Theme.status(msg, ex.getMessage(), false);
        }
    }

    private void onRemove() {
        int row = cartTable.getSelectedRow();
        if (row < 0) { Theme.status(msg, "Select a cart item to remove.", false); return; }
        try {
            String name = svc.getCart().getItems().get(row).getName();
            svc.removeFromCart(row);
            refreshCart();
            Theme.status(msg, "Removed " + name + " from the cart.", true);
        } catch (RuntimeException ex) {
            Theme.status(msg, ex.getMessage(), false);
        }
    }

    private void onPlace() {
        try {
            Object s = supplier.getEditor().getItem();
            Order o = svc.placeOrder(s == null ? "" : s.toString());
            JOptionPane.showMessageDialog(this,
                    "Order #" + o.getId() + " placed with " + o.getSupplier() + ".\nTotal: " + Theme.money(o.getTotal()),
                    "Order placed", JOptionPane.INFORMATION_MESSAGE);
            msg.setText(" ");
            app.refreshAll();
            app.show(FABRIX.PROGRESS);
        } catch (Exception ex) {
            Theme.status(msg, ex.getMessage(), false);
        }
    }

    private void refreshProducts() {
        shown = new ArrayList<>(svc.search(search.getText(), InventoryService.ALL_CATEGORIES, 0));
        productModel.setRowCount(0);
        for (Product p : shown) productModel.addRow(new Object[]{p, p.getSize(), p.getStock(), Theme.money(p.getPrice())});
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        for (CartItem i : svc.getCart().getItems()) {
            cartModel.addRow(new Object[]{i.getName(), i.getSize(), i.getQuantity(), Theme.money(i.getSubtotal())});
        }
        total.setText(Theme.money(svc.getCart().getTotal()));
    }

    @Override public void refresh() {
        refreshProducts();
        refreshCart();
    }
}
