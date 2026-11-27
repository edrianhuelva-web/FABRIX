package fabrix;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Order Progress: orders list + step tracker. */
public class ProgressPage extends Page {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private final DefaultTableModel model = Theme.model("Order #", "Placed", "Supplier", "Units", "Value", "Status");
    private final JTable table = Theme.table(model, 48, 0);
    private final JLabel detailTitle = Theme.label("Select an order", Theme.F_H2, Theme.TEXT);
    private final JLabel detailSub = Theme.label("Pick an order above to see where it is.", Theme.F_BASE, Theme.MUTED);
    private final StepTracker tracker = new StepTracker();
    private final JTextArea items = new JTextArea(3, 20);
    private final Theme.Btn advance = Theme.btn("Advance Status", Theme.Kind.PRIMARY);
    private final JLabel msg = Theme.label(" ", Theme.F_BASE, Theme.MUTED);

    private List<Order> shown = new ArrayList<>();

    public ProgressPage(InventoryService svc, DashboardFrame app) {
        super(svc, app);
        add(Theme.header("Order Progress", "Track every supplier order from pending to delivered.", null), BorderLayout.NORTH);

        table.getColumnModel().getColumn(0).setCellRenderer(new Theme.ColorCell(Theme.TEXT, true, 0));
        table.getColumnModel().getColumn(5).setCellRenderer(new Theme.StatusTextCell(0));
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) showDetail(); });

        items.setEditable(false);
        items.setBackground(Theme.BG);
        items.setForeground(Theme.TEXT);
        items.setFont(Theme.F_BASE);
        items.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        advance.addActionListener(e -> onAdvance());

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(detailTitle);
        titles.add(Box.createVerticalStrut(3));
        titles.add(detailSub);
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(titles, BorderLayout.WEST);
        head.add(advance, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout(0, 6));
        bottom.setOpaque(false);
        bottom.add(Theme.scroll(items), BorderLayout.CENTER);
        bottom.add(msg, BorderLayout.SOUTH);

        Theme.Card detail = new Theme.Card(new BorderLayout(0, 8));
        detail.add(head, BorderLayout.NORTH);
        detail.add(tracker, BorderLayout.CENTER);
        detail.add(bottom, BorderLayout.SOUTH);

        Theme.Card listCard = new Theme.Card(new BorderLayout(0, 14));
        listCard.add(Theme.label("All orders", Theme.F_H2, Theme.TEXT), BorderLayout.NORTH);
        listCard.add(Theme.scroll(table, false), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(0, 24));
        body.setOpaque(false);
        body.add(listCard, BorderLayout.CENTER);
        body.add(detail, BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);
    }

    private void onAdvance() {
        int row = table.getSelectedRow();
        if (row < 0) { Theme.status(msg, "Select an order first.", false); return; }
        try {
            Order o = shown.get(row);
            svc.advanceOrder(o);
            String id = o.getId();
            app.refreshAll();
            select(id);
            Theme.status(msg, o.getStatus().isFinal()
                    ? "Delivered! " + o.getUnits() + " units were added to your stock."
                    : "Order moved to: " + o.getStatus().getLabel() + ".", true);
        } catch (Exception ex) {
            Theme.status(msg, ex.getMessage(), false);
        }
    }

    private void select(String id) {
        for (int i = 0; i < shown.size(); i++) {
            if (shown.get(i).getId().equals(id)) { table.setRowSelectionInterval(i, i); return; }
        }
    }

    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= shown.size()) {
            detailTitle.setText("Select an order");
            detailSub.setText("Pick an order above to see where it is.");
            tracker.setStatus(null);
            items.setText("");
            advance.setEnabled(false);
            return;
        }
        Order o = shown.get(row);
        detailTitle.setText("Order #" + o.getId());
        detailSub.setText(o.getSupplier() + "  \u2022  placed " + o.getPlacedAt().format(FMT));
        tracker.setStatus(o.getStatus());
        advance.setEnabled(!o.getStatus().isFinal());
        StringBuilder sb = new StringBuilder();
        for (CartItem i : o.getItems()) {
            sb.append(i.getQuantity()).append(" x ").append(i.getName())
              .append(" (").append(i.getSize()).append(")   ").append(Theme.money(i.getSubtotal())).append('\n');
        }
        items.setText(sb.toString());
        items.setCaretPosition(0);
    }

    @Override public void refresh() {
        String keep = null;
        int sel = table.getSelectedRow();
        if (sel >= 0 && sel < shown.size()) keep = shown.get(sel).getId();

        shown = new ArrayList<>(svc.getOrders());
        java.util.Collections.reverse(shown);           // newest first
        model.setRowCount(0);
        for (Order o : shown) {
            model.addRow(new Object[]{"#" + o.getId(), o.getPlacedAt().format(FMT), o.getSupplier(),
                    o.getUnits(), Theme.money(o.getTotal()), o.getStatus()});
        }
        if (keep != null) select(keep);
        else if (!shown.isEmpty()) table.setRowSelectionInterval(0, 0);
        showDetail();
    }

    /** Horizontal step progress bar. */
    private static class StepTracker extends JComponent {
        private OrderStatus status;

        StepTracker() { setPreferredSize(new Dimension(100, 84)); }

        void setStatus(OrderStatus s) { status = s; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            OrderStatus[] all = OrderStatus.values();
            int n = all.length, margin = 90, w = getWidth(), cy = 22, r = 11;
            if (w < 2 * margin + 20) { g2.dispose(); return; }
            double gap = (w - 2.0 * margin) / (n - 1);
            int cur = status == null ? -1 : status.ordinal();
            boolean complete = status != null && status.isFinal();

            g2.setStroke(new BasicStroke(3f));
            for (int i = 0; i < n - 1; i++) {
                g2.setColor(i < cur ? Theme.GOLD : Theme.BORDER);
                g2.drawLine((int) (margin + i * gap) + r, cy, (int) (margin + (i + 1) * gap) - r, cy);
            }
            g2.setFont(Theme.F_BOLD);
            FontMetrics fm = g2.getFontMetrics();
            for (int i = 0; i < n; i++) {
                int cx = (int) (margin + i * gap);
                boolean done = complete ? i <= cur : i < cur;
                boolean now = i == cur && !complete;
                if (done) {
                    g2.setColor(Theme.GOLD);
                    g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
                    g2.setColor(Theme.TEXT);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(cx - 4, cy, cx - 1, cy + 3);
                    g2.drawLine(cx - 1, cy + 3, cx + 5, cy - 3);
                } else if (now) {
                    g2.setColor(Theme.GOLD);
                    g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
                    g2.setColor(Theme.TEXT);
                    g2.fillOval(cx - 4, cy - 4, 8, 8);
                } else {
                    g2.setColor(Theme.CARD);
                    g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
                    g2.setColor(new Color(0xD3D0C7));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);
                }
                String label = all[i].getLabel();
                g2.setColor(i <= cur ? Theme.TEXT : Theme.MUTED);
                g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + r + 22);
            }
            g2.dispose();
        }
    }
}
