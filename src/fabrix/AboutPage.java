package fabrix;

import java.awt.*;
import javax.swing.*;

/** About Website. */
public class AboutPage extends Page {
    public AboutPage(InventoryService svc, DashboardFrame app) {
        super(svc, app);
        add(Theme.header("About", null, null), BorderLayout.NORTH);

        String html = "<html><body style='width:620px;font-family:Segoe UI;font-size:13px;color:#6B6B6B'>"
                + "<p style='color:#1A1A1A;font-size:16px'><b>FABRIX: Centralized Fashion Inventory Management System</b></p>"
                + "<p>FABRIX keeps every piece of your clothing line in one place. Track stock levels by size, "
                + "restock from suppliers and follow each order until it arrives, with no spreadsheets to chase.</p>"
                + "<p style='color:#8A6D1D'><b>What you can do</b></p>"
                + "<ul>"
                + "<li><b>Dashboard:</b> total items, low stock alerts, pending orders and recent activity</li>"
                + "<li><b>View Stock:</b> every item with size, price and stock status</li>"
                + "<li><b>Search Clothes and Search Filter:</b> search by name, SKU, collection or size, then filter by category and stock level</li>"
                + "<li><b>Add Clothes:</b> register a new piece in your collection</li>"
                + "<li><b>Remove Clothes:</b> select an item in View Stock and remove it</li>"
                + "<li><b>Order Clothes:</b> build a cart and place a restock order with a supplier</li>"
                + "<li><b>Order Progress:</b> follow each order from Pending to Delivered</li>"
                + "</ul>"
                + "<p style='color:#8A6D1D'><b>How it stores data</b></p>"
                + "<p>Everything is saved in plain CSV files inside the <i>data</i> folder "
                + "(products.csv and orders.csv), so you can also open them in Excel.</p>"
                + "<p>When an order reaches <i>Delivered</i>, its units are added to your stock automatically.</p>"
                + "</body></html>";
        JLabel text = new JLabel(html);
        text.setForeground(Theme.MUTED);
        Theme.Card card = new Theme.Card(new BorderLayout());
        card.add(text, BorderLayout.NORTH);
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(false);
        wrap.add(card);
        add(wrap, BorderLayout.CENTER);
    }

    @Override public void refresh() {}
}
