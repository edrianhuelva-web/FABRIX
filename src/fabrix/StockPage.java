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
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Item", "Category", "Size", "Price", "In Stock", "Status", "Action"}, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {

        // Only the Action column can be clicked
        return column == 6;
    }
};
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
        
        // Configure Edit button column
        table.getColumnModel().getColumn(6).setCellRenderer(new EditButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new EditButtonEditor());

        // Set Action column width
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setMinWidth(90);
        table.getColumnModel().getColumn(6).setMaxWidth(90);

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
            model.addRow(new Object[]{p, p.getCategory(), p.getSize(), Theme.money(p.getPrice()), p, p, p});
        }
        int total = svc.getProducts().size();
        footer.setText(shown.isEmpty() ? "No clothes match your search."
                : "Showing " + shown.size() + " of " + total + " items");
    }
    
    private void editProduct(Product p) {

        // Input fields populated with existing product information
        JTextField name = new JTextField(p.getName());
        JTextField collection = new JTextField(p.getCollection());
        JTextField price = new JTextField(
                String.valueOf(p.getPrice())
        );
        JTextField stock = new JTextField(
                String.valueOf(p.getStock())
        );

        // Category dropdown
        JComboBox<String> category = new JComboBox<>();

        for (String c : svc.getCategories()) {
            category.addItem(c);
        }

        category.setEditable(true);
        category.setSelectedItem(p.getCategory());

        // Size dropdown
        JComboBox<String> size = new JComboBox<>(
                new String[]{"XS", "S", "M", "L", "XL", "XXL", "One Size"}
        );

        size.setEditable(true);
        size.setSelectedItem(p.getSize());

        // Form layout
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));

        form.add(new JLabel("Clothing Name:"));
        form.add(name);

        form.add(new JLabel("Category:"));
        form.add(category);

        form.add(new JLabel("Collection:"));
        form.add(collection);

        form.add(new JLabel("Size:"));
        form.add(size);

        form.add(new JLabel("Price (₱):"));
        form.add(price);

        form.add(new JLabel("Current Stock:"));
        form.add(stock);

        // Display dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit Clothing Information",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            // Get selected dropdown values
            Object selectedCategory = category.getEditor().getItem();
            Object selectedSize = size.getEditor().getItem();

            // Update the product
            svc.updateProduct(
                    p,
                    name.getText(),
                    selectedCategory == null ? "" : selectedCategory.toString(),
                    collection.getText(),
                    selectedSize == null ? "" : selectedSize.toString(),
                    price.getText(),
                    stock.getText()
            );

            // Refresh the inventory table
            refresh();

            // Refresh dashboard information
            app.refreshAll();

            JOptionPane.showMessageDialog(
                    this,
                    "Product information updated successfully!",
                    "FABRIX",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    // Custom compact Edit button
    private class EditCellButton extends JButton {

        private boolean selected;

        public EditCellButton() {
            super("Edit");

            setFont(Theme.F_BOLD);
            setForeground(Theme.TEXT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void setSelectedRow(boolean selected) {
            this.selected = selected;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Draw table cell background
            g2.setColor(selected ? Theme.HOVER : Theme.CARD);

            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw compact gold button
            int buttonWidth = Math.min(72, getWidth() - 8);
            int buttonHeight = Math.min(34, getHeight() - 8);

            int x = (getWidth() - buttonWidth) / 2;
            int y = (getHeight() - buttonHeight) / 2;

            Color buttonColor = getModel().isPressed()
                    ? Theme.GOLD_DARK
                    : Theme.GOLD;

            g2.setColor(buttonColor);

            g2.fillRoundRect(
                    x, y,
                    buttonWidth, buttonHeight,
                    8, 8
            );

            // Draw button text
            g2.setColor(Theme.TEXT);
            g2.setFont(Theme.F_BOLD);

            FontMetrics fm = g2.getFontMetrics();

            String text = "Edit";

            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2
                    + fm.getAscent();

            g2.drawString(text, textX, textY);

            g2.dispose();
        }
    }

// Displays the Edit button
    private class EditButtonRenderer implements
            javax.swing.table.TableCellRenderer {

        private final EditCellButton button = new EditCellButton();

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            button.setSelectedRow(isSelected);

            return button;
        }
    }

// Handles Edit button clicks
    private class EditButtonEditor
            extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {

        private final EditCellButton button = new EditCellButton();

        private Product selectedProduct;

        public EditButtonEditor() {

            button.addActionListener(e -> {

                Product product = selectedProduct;

                fireEditingStopped();

                SwingUtilities.invokeLater(() -> {
                    if (product != null) {
                        editProduct(product);
                    }
                });
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {

            selectedProduct = (Product) value;

            button.setSelectedRow(true);

            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return selectedProduct;
        }
    }
}
