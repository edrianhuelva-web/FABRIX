package fabrix;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/** FABRIX theme: dark sidebar, cream background, white cards, yellow accent. */
public final class Theme {
    private Theme() {}

    // Sidebar
    public static final Color SIDEBAR       = new Color(0x1A1A1A);
    public static final Color SIDEBAR_HOVER = new Color(0x2A2A2A);
    public static final Color SIDEBAR_TEXT  = new Color(0xC9C9C9);
    public static final Color SIDEBAR_LINE  = new Color(0x333333);

    // Content
    public static final Color BG     = new Color(0xF6F5F1);
    public static final Color CARD   = Color.WHITE;
    public static final Color FIELD  = Color.WHITE;
    public static final Color BORDER = new Color(0xE4E2DB);
    public static final Color HOVER  = new Color(0xFBF3D6);   // selected row / hovered item
    public static final Color TEXT   = new Color(0x1A1A1A);
    public static final Color MUTED  = new Color(0x6B6B6B);
    public static final Color GOLD   = new Color(0xECC237);
    public static final Color GOLD_DARK = new Color(0xC9A227);

    // Status colors
    public static final Color DANGER = new Color(0xD64545);
    public static final Color GREEN  = new Color(0x2E7D4F);
    public static final Color BLUE   = new Color(0x2F6AA3);
    public static final Color PURPLE = new Color(0x7A4FA0);
    public static final Color AMBER  = new Color(0x8A6D1D);
    public static final Color ORANGE = new Color(0xC77D14);

    // Fonts
    public static final Font F_BASE  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_BOLD  = F_BASE.deriveFont(Font.BOLD);
    public static final Font F_SMALL = F_BASE.deriveFont(12f);
    public static final Font F_H2    = F_BASE.deriveFont(Font.BOLD, 17f);
    public static final Font F_TITLE = F_BASE.deriveFont(Font.BOLD, 28f);

    public static void install() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("Panel.background", CARD);
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", F_BASE);
        UIManager.put("OptionPane.buttonFont", F_BASE);
        UIManager.put("Button.background", new Color(0xEFEDE6));
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.select", BORDER);
        UIManager.put("Button.disabledText", new Color(0x9A9A9A));
        UIManager.put("ComboBox.background", FIELD);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.disabledBackground", FIELD);
        UIManager.put("ComboBox.selectionBackground", HOVER);
        UIManager.put("ComboBox.selectionForeground", TEXT);
        UIManager.put("ComboBox.buttonBackground", FIELD);
        UIManager.put("ComboBox.buttonShadow", FIELD);
        UIManager.put("ComboBox.buttonDarkShadow", MUTED);
        UIManager.put("ComboBox.buttonHighlight", FIELD);
        UIManager.put("ToolTip.background", CARD);
        UIManager.put("ToolTip.foreground", TEXT);
    }

    // ---------- small helpers ----------
    public static String money(double v) { return String.format("\u20B1%,.2f", v); }

    public static JLabel label(String text, Font f, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(f);
        l.setForeground(c);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static <T extends JComponent> T size(T c, int w, int h) {
        c.setPreferredSize(new Dimension(w, h));
        return c;
    }

    public static void status(JLabel l, String msg, boolean ok) {
        l.setForeground(ok ? GREEN : DANGER);
        l.setText(msg);
    }

    public static DocumentListener onChange(Runnable r) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { r.run(); }
            public void removeUpdate(DocumentEvent e) { r.run(); }
            public void changedUpdate(DocumentEvent e) { r.run(); }
        };
    }

    /** Page title (and optional subtitle) on the left, optional component on the right. */
    public static JPanel header(String title, String sub, JComponent right) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(label(title, F_TITLE, TEXT));
        if (sub != null) {
            left.add(Box.createVerticalStrut(4));
            left.add(label(sub, F_BASE, MUTED));
        }
        p.add(left, BorderLayout.WEST);
        if (right != null) {
            JPanel wrap = new JPanel(new BorderLayout());   // keeps the button its own height
            wrap.setOpaque(false);
            wrap.add(right, BorderLayout.NORTH);
            p.add(wrap, BorderLayout.EAST);
        }
        return p;
    }

    public static Color statusColor(OrderStatus s) {
        switch (s) {
            case PENDING:    return AMBER;
            case PROCESSING: return BLUE;
            case SHIPPED:    return PURPLE;
            default:         return GREEN;
        }
    }

    // ---------- Buttons ----------
    public enum Kind { PRIMARY, DANGER, GHOST }

    public static Btn btn(String text, Kind kind) { return new Btn(text, kind); }

    public static class Btn extends JButton {
        private final Kind kind;

        public Btn(String text, Kind kind) {
            super(text);
            this.kind = kind;
            setFont(F_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 20));
            setForeground(kind == Kind.DANGER ? Color.WHITE : TEXT);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = kind == Kind.PRIMARY ? GOLD : kind == Kind.DANGER ? DANGER : Color.WHITE;
            if (!isEnabled()) base = new Color(0xEAE8E1);
            else if (getModel().isPressed()) base = kind == Kind.GHOST ? new Color(0xE9E7E0) : base.darker();
            else if (getModel().isRollover())
                base = kind == Kind.PRIMARY ? new Color(0xF4D05A) : kind == Kind.DANGER ? DANGER.brighter() : new Color(0xF3F1EB);
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            if (kind == Kind.GHOST) {
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------- Card panel ----------
    public static class Card extends JPanel {
        public Card(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class StatCard extends Card {
        private final JLabel value = label("0", F_BASE.deriveFont(Font.BOLD, 32f), TEXT);
        private final JLabel note = label(" ", F_SMALL, MUTED);

        public StatCard(String title) {
            super(null);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(label(title, F_BASE, MUTED));
            add(Box.createVerticalStrut(8));
            add(value);
            add(Box.createVerticalStrut(18));
            add(note);
        }

        public void set(String v, String n) {
            value.setText(v);
            note.setText(n);
        }
    }

    // ---------- Inputs ----------
    public static Field field(String hint) { return new Field(hint); }

    public static class Field extends JTextField {
        private final String hint;
        private final Border normal = BorderFactory.createCompoundBorder(
                new LineBorder(BORDER), BorderFactory.createEmptyBorder(8, 10, 8, 10));
        private final Border focused = BorderFactory.createCompoundBorder(
                new LineBorder(GOLD_DARK), BorderFactory.createEmptyBorder(8, 10, 8, 10));

        public Field(String hint) {
            this.hint = hint;
            setFont(F_BASE);
            setForeground(TEXT);
            setBackground(FIELD);
            setCaretColor(TEXT);
            setSelectionColor(new Color(0xF3E3A0));
            setSelectedTextColor(TEXT);
            setBorder(normal);
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { setBorder(focused); }
                @Override public void focusLost(FocusEvent e) { setBorder(normal); }
            });
        }

        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = Math.max(d.height, 40);
            return d;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (hint != null && getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(0x9A9A9A));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                Insets in = getInsets();
                g2.drawString(hint, in.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    public static JComboBox<String> combo(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        styleCombo(c);
        return c;
    }

    public static <T> void styleCombo(JComboBox<T> c) {
        c.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                BasicArrowButton b = new BasicArrowButton(SwingConstants.SOUTH, FIELD, FIELD, MUTED, FIELD);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
        });
        c.setBackground(FIELD);
        c.setForeground(TEXT);
        c.setFont(F_BASE);
        c.setBorder(new LineBorder(BORDER));
        c.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, false, false);
                setBackground(isSelected && index >= 0 ? HOVER : FIELD);
                setForeground(TEXT);
                setFont(F_BASE);
                setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                return this;
            }
        });
        if (c.isEditable() && c.getEditor().getEditorComponent() instanceof JTextField) {
            JTextField tf = (JTextField) c.getEditor().getEditorComponent();
            tf.setBackground(FIELD);
            tf.setForeground(TEXT);
            tf.setCaretColor(TEXT);
            tf.setFont(F_BASE);
            tf.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        }
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, 40));
    }

    // ---------- Tables ----------
    public static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    /** pad = left padding of every cell (0 lines the table up with a card's title). */
    public static JTable table(DefaultTableModel m, int rowHeight, int pad) {
        JTable t = new JTable(m);
        t.setBackground(CARD);
        t.setForeground(TEXT);
        t.setFont(F_BASE);
        t.setGridColor(BORDER);
        t.setShowGrid(false);
        t.setShowHorizontalLines(true);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setRowHeight(rowHeight);
        t.setSelectionBackground(HOVER);
        t.setSelectionForeground(TEXT);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.setBorder(null);
        t.setDefaultRenderer(Object.class, new ColorCell(TEXT, false, pad));
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 38));
        h.setDefaultRenderer(new HeaderCell(pad));
        h.setBackground(CARD);
        return t;
    }

    public static JScrollPane scroll(Component view, boolean bordered) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(bordered ? new LineBorder(BORDER) : BorderFactory.createEmptyBorder());
        sp.setBackground(CARD);
        sp.getViewport().setBackground(CARD);
        sp.getVerticalScrollBar().setUI(new ThinScroll());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        sp.getHorizontalScrollBar().setUI(new ThinScroll());
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        JPanel corner = new JPanel();
        corner.setBackground(CARD);
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        return sp;
    }

    public static JScrollPane scroll(Component view) { return scroll(view, true); }

    private static class ThinScroll extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(0xD3D0C7);
            trackColor = CARD;
        }
        @Override protected JButton createDecreaseButton(int o) { return zero(); }
        @Override protected JButton createIncreaseButton(int o) { return zero(); }
        private JButton zero() {
            JButton b = new JButton();
            Dimension d = new Dimension(0, 0);
            b.setPreferredSize(d); b.setMinimumSize(d); b.setMaximumSize(d);
            return b;
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(CARD);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
            g2.dispose();
        }
    }

    // ---------- Table cell renderers ----------
    /** Plain text cell with a chosen color / weight. Subclasses turn a value into text + color. */
    public static class ColorCell extends DefaultTableCellRenderer {
        private final Color fg;
        private final boolean bold;
        private final int pad;

        public ColorCell(Color fg, boolean bold, int pad) {
            this.fg = fg;
            this.bold = bold;
            this.pad = pad;
        }

        protected String text(Object v) { return v == null ? "" : v.toString(); }
        protected Color color(Object v) { return fg; }

        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, text(v), false, false, r, c);
            setBorder(BorderFactory.createEmptyBorder(0, pad, 0, 12));
            setBackground(sel ? HOVER : CARD);
            setForeground(color(v));
            setFont(bold ? F_BOLD : F_BASE);
            return this;
        }
    }

    /** Value must be an OrderStatus: colored bold label. */
    public static class StatusTextCell extends ColorCell {
        public StatusTextCell(int pad) { super(TEXT, true, pad); }
        @Override protected String text(Object v) { return ((OrderStatus) v).getLabel(); }
        @Override protected Color color(Object v) { return statusColor((OrderStatus) v); }
    }

    /** Value must be a Product: "In Stock" / "Low Stock" / "Out of Stock" in color. */
    public static class LevelTextCell extends ColorCell {
        public LevelTextCell(int pad) { super(TEXT, true, pad); }
        @Override protected String text(Object v) { return ((Product) v).getLevel().getLabel(); }
        @Override protected Color color(Object v) {
            switch (((Product) v).getLevel()) {
                case IN_STOCK:  return GREEN;
                case LOW_STOCK: return ORANGE;
                default:        return DANGER;
            }
        }
    }

    /** Value must be a Product: units left, red when low or out. */
    public static class QtyCell extends ColorCell {
        public QtyCell(int pad) { super(TEXT, true, pad); }
        @Override protected String text(Object v) { return String.format("%,d", ((Product) v).getStock()); }
        @Override protected Color color(Object v) {
            return ((Product) v).getLevel() == Product.Level.IN_STOCK ? TEXT : DANGER;
        }
    }

    private static class HeaderCell extends DefaultTableCellRenderer {
        private final int pad;
        HeaderCell(int pad) { this.pad = pad; }

        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v == null ? "" : v.toString(), false, false, r, c);
            setBackground(CARD);
            setForeground(MUTED);
            setFont(F_SMALL.deriveFont(Font.BOLD));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    BorderFactory.createEmptyBorder(0, pad, 0, 12)));
            return this;
        }
    }

    /** Value must be a Product: bold name over muted SKU. */
    public static class ProductCell extends JPanel implements TableCellRenderer {
        private final JLabel name = new JLabel();
        private final JLabel sku = new JLabel();

        public ProductCell(int pad) {
            super(new GridLayout(2, 1, 0, 0));
            name.setFont(F_BOLD); name.setForeground(TEXT); name.setVerticalAlignment(SwingConstants.BOTTOM);
            sku.setFont(F_SMALL); sku.setForeground(MUTED); sku.setVerticalAlignment(SwingConstants.TOP);
            setBorder(BorderFactory.createEmptyBorder(0, pad, 0, 12));
            add(name);
            add(sku);
        }

        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            Product p = (Product) v;
            name.setText(p.getName());
            sku.setText(p.getSku());
            setBackground(sel ? HOVER : CARD);
            return this;
        }
    }
}
