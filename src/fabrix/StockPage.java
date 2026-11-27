package fabrix;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** View Stock: Search Clothes, Search Filter and Remove Clothes. */
public class StockPage extends Page {
    private final JTextField search = Theme.field("Search clothes, SKU, collection, size...");
    private final JComboBox<String> category = Theme.combo(InventoryService.ALL_CATEGORIES);
    private final JComboBox<String> level = Theme.combo(InventoryService.LEVELS);
    private final DefaultTableModel model = Theme.model("Item", "Category", "Size", "Price", "In Stock", "Status");
    private final JTable table = Theme.table(model, 58, 0);
    private final JLabel footer = Theme.label(" ", Theme.F_SMALL, Theme.MUTED);
    private final JLabel msg = Theme.label(" ", Theme.F_BASE, Theme.MUTED);
    private List<Product> shown = new ArrayList<>();
    private boolean updating;

    public StockPage(InventoryService svc, DashboardFrame app) {
        super(svc, app);

        Theme.Btn add = Theme.btn("+ Add Clothes", Theme.Kind.PRIMARY);
        add.addActionListener(e -> app.show(FABRIX.ADD));
        add(Theme.header("View Stock", "Search, filter and manage every piece in your collection.", add), BorderLayout.NORTH);

        Theme.size(category, 180, 40);
        Theme.size(level, 180, 40);
        Theme.Btn clear = Theme.btn("Clear", Theme.Kind.GHOST);
        clear.addActionListener(e -> {
            updating = true;
            search.setText("");
            category.setSelectedIndex(0);
            level.setSelectedIndex(0);
            updating = false;
            applyFilter();
        });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(category); right.add(level); right.add(clear);
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.add(search, BorderLayout.CENTER);
        toolbar.add(right, BorderLayout.EAST);

        table.getColumnModel().getColumn(0).setCellRenderer(new Theme.ProductCell(0));
        table.getColumnModel().getColumn(4).setCellRenderer(new Theme.QtyCell(0));
        table.getColumnModel().getColumn(5).setCellRenderer(new Theme.LevelTextCell(0));
        table.getColumnModel().getColumn(0).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        Theme.Btn remove = Theme.btn("Remove Selected", Theme.Kind.DANGER);
        remove.addActionListener(e -> onRemove());
        JPanel foot = new JPanel(new BorderLayout(12, 0));
        foot.setOpaque(false);
        JPanel footText = new JPanel();
        footText.setOpaque(false);
        footText.setLayout(new BoxLayout(footText, BoxLayout.Y_AXIS));
        footText.add(footer);
        footText.add(Box.createVerticalStrut(2));
        footText.add(msg);
        foot.add(footText, BorderLayout.CENTER);
        foot.add(remove, BorderLayout.EAST);

        Theme.Card card = new Theme.Card(new BorderLayout(0, 16));
        card.add(toolbar, BorderLayout.NORTH);
        card.add(Theme.scroll(table, false), BorderLayout.CENTER);
        card.add(foot, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);

        search.getDocument().addDocumentListener(Theme.onChange(this::applyFilter));
        category.addActionListener(e -> { if (!updating) applyFilter(); });
        level.addActionListener(e -> { if (!updating) applyFilter(); });
    }

    private void onRemove() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= shown.size()) { Theme.status(msg, "Select an item in the table to remove.", false); return; }
        Product p = shown.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Remove \"" + p.getName() + "\" (size " + p.getSize() + ", " + p.getSku() + ") from inventory?\nThis can't be undone.",
                "Remove clothes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            svc.removeProduct(p);
            app.refreshAll();
            Theme.status(msg, "Removed \"" + p.getName() + "\" (" + p.getSize() + ").", true);
        } catch (Exception ex) {
            Theme.status(msg, ex.getMessage(), false);
        }
    }

    @Override public void refresh() {
        updating = true;
        String sel = (String) category.getSelectedItem();
        category.removeAllItems();
        category.addItem(InventoryService.ALL_CATEGORIES);
        for (String c : svc.getCategories()) category.addItem(c);
        if (sel != null) category.setSelectedItem(sel);
        updating = false;
        applyFilter();
    }

    private void applyFilter() {
        shown = new ArrayList<>(svc.search(search.getText(),
                (String) category.getSelectedItem(), level.getSelectedIndex()));
        model.setRowCount(0);
        for (Product p : shown) {
            model.addRow(new Object[]{p, p.getCategory(), p.getSize(), Theme.money(p.getPrice()), p, p});
        }
        int total = svc.getProducts().size();
        footer.setText(shown.isEmpty() ? "No clothes match your search."
                : "Showing " + shown.size() + " of " + total + " items");
    }
}
