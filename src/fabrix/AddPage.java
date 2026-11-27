package fabrix;

import java.awt.*;
import javax.swing.*;

/** Add Clothes form. */
public class AddPage extends Page {
    private final JTextField name = Theme.field("e.g. Linen Shirt");
    private final JComboBox<String> category = new JComboBox<>();
    private final JTextField collection = Theme.field("e.g. Linen / SS26");
    private final JComboBox<String> size = new JComboBox<>(new String[]{"XS", "S", "M", "L", "XL", "One Size"});
    private final JTextField price = Theme.field("e.g. 1290");
    private final JTextField stock = Theme.field("e.g. 50");
    private final JLabel msg = Theme.label(" ", Theme.F_BASE, Theme.MUTED);

    public AddPage(InventoryService svc, DashboardFrame app) {
        super(svc, app);
        add(Theme.header("Add Clothes", "Register a new piece in your collection.", null), BorderLayout.NORTH);

        category.setEditable(true);
        Theme.styleCombo(category);
        size.setEditable(true);
        Theme.styleCombo(size);
        size.setSelectedItem("M");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        put(form, 0, 0, 2, "Clothing name", name);
        put(form, 1, 0, 1, "Category", category);
        put(form, 1, 1, 1, "Collection", collection);
        put(form, 2, 0, 1, "Size", size);
        put(form, 2, 1, 1, "Price (\u20B1)", price);
        put(form, 3, 0, 1, "Initial stock (units)", stock);

        Theme.Btn addBtn = Theme.btn("Add Clothes", Theme.Kind.PRIMARY);
        addBtn.addActionListener(e -> onAdd());
        Theme.Btn clear = Theme.btn("Clear", Theme.Kind.GHOST);
        clear.addActionListener(e -> clearForm());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.setOpaque(false);
        buttons.add(addBtn);
        buttons.add(Box.createHorizontalStrut(8));
        buttons.add(clear);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 8; gc.gridwidth = 2; gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(6, 0, 0, 0);
        form.add(buttons, gc);
        gc.gridy = 9; gc.insets = new Insets(12, 0, 0, 0);
        form.add(msg, gc);

        Theme.Card card = new Theme.Card(new BorderLayout());
        card.add(form, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(660, card.getPreferredSize().height));
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(false);
        wrap.add(card);
        add(wrap, BorderLayout.CENTER);
    }

    private void put(JPanel form, int row, int col, int span, String label, JComponent field) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = col; gc.gridwidth = span; gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL; gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(0, col == 1 ? 14 : 0, 0, 0);
        gc.gridy = row * 2;
        form.add(Theme.label(label, Theme.F_SMALL, Theme.MUTED), gc);
        gc.gridy = row * 2 + 1;
        gc.insets = new Insets(4, col == 1 ? 14 : 0, 16, 0);
        form.add(field, gc);
    }

    private void onAdd() {
        try {
            Object c = category.getEditor().getItem();
            Object s = size.getEditor().getItem();
            Product p = svc.addProduct(name.getText(), c == null ? "" : c.toString(),
                    collection.getText(), s == null ? "" : s.toString(), price.getText(), stock.getText());
            clearForm();
            Theme.status(msg, "Added \"" + p.getName() + "\" (" + p.getSize() + ", " + p.getSku() + ") to your collection.", true);
            app.refreshAll();
        } catch (Exception ex) {
            Theme.status(msg, ex.getMessage(), false);
        }
    }

    private void clearForm() {
        name.setText(""); collection.setText(""); price.setText(""); stock.setText("");
        category.setSelectedItem("");
        size.setSelectedItem("M");
        msg.setText(" ");
    }

    @Override public void refresh() {
        Object keep = category.getEditor().getItem();
        category.removeAllItems();
        java.util.List<String> cats = new java.util.ArrayList<>(svc.getCategories());
        for (String d : new String[]{"Tops", "Bottoms", "Outerwear", "Dresses", "Accessories"}) {
            boolean has = false;
            for (String c : cats) if (c.equalsIgnoreCase(d)) has = true;
            if (!has) cats.add(d);
        }
        java.util.Collections.sort(cats, String.CASE_INSENSITIVE_ORDER);
        for (String c : cats) category.addItem(c);
        category.setSelectedItem(keep == null ? "" : keep.toString());
    }
}
