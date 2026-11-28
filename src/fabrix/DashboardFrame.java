/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package fabrix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Pacunduu
 */
public class DashboardFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardFrame.class.getName());
    private InventoryService service;

    /**
     * Creates new form DashboardFrame
     */
    public DashboardFrame() {
        initComponents();
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/fabrix/logo.png")).getImage());
        styleTables();
        styleSidebar();
        
        try {
            service = new InventoryService();
            loadDashboardData();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }
    
    private void styleSidebar() {
        javax.swing.JButton[] navButtons = {jButton1, jButton2, jButton3, jButton4};
        for (javax.swing.JButton btn : navButtons) {
            btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                @Override
                public void paint(java.awt.Graphics g, javax.swing.JComponent c) {
                    javax.swing.AbstractButton b = (javax.swing.AbstractButton) c;
                    if (Boolean.TRUE.equals(b.getClientProperty("active"))) {
                        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(238, 198, 67));
                        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                        g2.dispose();
                    }
                    super.paint(g, c);
                }
            });
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            btn.setForeground(new Color(220, 220, 220));
            btn.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
            btn.setMinimumSize(new Dimension(190, 40));
            btn.setPreferredSize(new Dimension(190, 40));
            btn.setMaximumSize(new Dimension(190, 40));
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void updateActiveButton(String pageName) {
        Color activeBg = new Color(238, 198, 67);
        Color inactiveBg = new Color(20, 20, 20);
        Color activeText = new Color(20, 20, 20);
        Color inactiveText = new Color(220, 220, 220);

        boolean isDash = pageName.equals(DASHBOARD);
        roundedPanel1.setBackground(isDash ? activeBg : inactiveBg);
        roundedPanel1.setBorderColor(isDash ? activeBg : inactiveBg);
        roundedPanel1.repaint();
        jLabel3.setForeground(isDash ? activeText : inactiveText);
        jLabel3.setFont(new Font("SansSerif", isDash ? Font.BOLD : Font.PLAIN, 13));

        javax.swing.JButton[] btns = {jButton1, jButton2, jButton3, jButton4};
        String[] keys = {STOCK, ADD, ORDER, PROGRESS};

        for (int i = 0; i < btns.length; i++) {
            boolean active = pageName.equals(keys[i]);
            btns[i].setOpaque(active);
            btns[i].setBackground(active ? activeBg : inactiveBg);
            btns[i].setForeground(active ? activeText : inactiveText);
            btns[i].setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 13));
            btns[i].repaint();
        }
    }
    public void loadDashboardData() {
        lblTotalItems.setText(String.format("%,d", service.totalUnits()));
        lblLowStock.setText(String.valueOf(service.lowStockAlerts()));
        lblPendingOrders.setText(String.valueOf(service.pendingOrders()));

        DefaultTableModel lowStockModel = (DefaultTableModel) tblLowStock.getModel();
        lowStockModel.setRowCount(0);
        for (Product p : service.lowStockItems(5)) {
            lowStockModel.addRow(new Object[]{ p.getName(), p.getSize(), p.getStock() });
        }

        DefaultTableModel ordersModel = (DefaultTableModel) tblRecentOrders.getModel();
        ordersModel.setRowCount(0);
        for (Order o : service.recentOrders(5)) {
            ordersModel.addRow(new Object[]{ o.getId(), o.getSupplier(), o.getStatus().toString() });
        }
    }
    public void refreshAll() {
        loadDashboardData();
    }
    
    public static final String DASHBOARD = "dashboard";
    public static final String STOCK = "stock";
    public static final String ADD = "add";
    public static final String ORDER = "order";
    public static final String PROGRESS = "progress";
    public static final String ABOUT = "about";

    private java.awt.Component[] dashboardViews;
    private java.awt.LayoutManager dashboardLayout;
    private StockPage stockPage;
    private AddPage addPage;
    private OrderPage orderPage;
    private ProgressPage progressPage;
    private AboutPage aboutPage;

    public void show(String pageName) {
        updateActiveButton(pageName);
        
        if (dashboardViews == null) {
            dashboardViews = mainContentPanel.getComponents();
            dashboardLayout = mainContentPanel.getLayout();
        }

        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new java.awt.BorderLayout());

        switch (pageName) {
            case STOCK -> {
                if (stockPage == null) stockPage = new StockPage(service, this);
                stockPage.refresh();
                mainContentPanel.add(stockPage, java.awt.BorderLayout.CENTER);
            }
            case ADD -> {
                if (addPage == null) addPage = new AddPage(service, this);
                addPage.refresh();
                mainContentPanel.add(addPage, java.awt.BorderLayout.CENTER);
            }
            case ORDER -> {
                if (orderPage == null) orderPage = new OrderPage(service, this);
                orderPage.refresh();
                mainContentPanel.add(orderPage, java.awt.BorderLayout.CENTER);
            }
            case PROGRESS -> {
                if (progressPage == null) progressPage = new ProgressPage(service, this);
                progressPage.refresh();
                mainContentPanel.add(progressPage, java.awt.BorderLayout.CENTER);
            }
            case ABOUT -> {
                if (aboutPage == null) aboutPage = new AboutPage(service, this);
                aboutPage.refresh();
                mainContentPanel.add(aboutPage, java.awt.BorderLayout.CENTER);
            }
            default -> {
                loadDashboardData();
                mainContentPanel.setLayout(dashboardLayout);
                for (java.awt.Component c : dashboardViews) {
                    mainContentPanel.add(c);
                }
            }
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    private void styleTables() {
        
    jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
    jScrollPane1.getViewport().setBackground(Color.WHITE);
    jScrollPane2.setBorder(BorderFactory.createEmptyBorder());
    jScrollPane2.getViewport().setBackground(Color.WHITE);

    JTable[] tables = {tblLowStock, tblRecentOrders};
    for (JTable table : tables) {
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(new Color(115, 115, 115));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        table.setDefaultEditor(Object.class, null);
        table.setDefaultEditor(String.class, null);
        
        table.setSelectionBackground(new Color(245, 245, 245));
        table.setSelectionForeground(new Color(30, 30, 30));
    }
    
    StatusCellRenderer statusRenderer = new StatusCellRenderer();
    tblLowStock.getColumnModel().getColumn(0).setCellRenderer(statusRenderer);
    tblLowStock.getColumnModel().getColumn(2).setCellRenderer(statusRenderer);
    
    tblRecentOrders.getColumnModel().getColumn(0).setCellRenderer(statusRenderer);
    tblRecentOrders.getColumnModel().getColumn(2).setCellRenderer(statusRenderer);
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        roundedPanel1 = new fabrix.RoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        mainContentPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        roundedPanel2 = new fabrix.RoundedPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        roundedPanel4 = new fabrix.RoundedPanel();
        jLabel11 = new javax.swing.JLabel();
        lblTotalItems = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        roundedPanel5 = new fabrix.RoundedPanel();
        jLabel12 = new javax.swing.JLabel();
        lblLowStock = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        roundedPanel3 = new fabrix.RoundedPanel();
        jLabel15 = new javax.swing.JLabel();
        lblPendingOrders = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        roundedPanel6 = new fabrix.RoundedPanel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLowStock = new javax.swing.JTable();
        roundedPanel7 = new fabrix.RoundedPanel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRecentOrders = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FABRIX");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        sidebarPanel.setBackground(new java.awt.Color(20, 20, 20));
        sidebarPanel.setPreferredSize(new java.awt.Dimension(230, 800));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(238, 198, 67));
        jLabel1.setText("FABRIX");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 0));

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 9)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(160, 160, 160));
        jLabel2.setText("INVENTORY MANAGEMENT");
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 0));

        roundedPanel1.setBackground(new java.awt.Color(238, 198, 67));
        roundedPanel1.setBorderColor(new java.awt.Color(238, 198, 67));
        roundedPanel1.setCornerRadius(10);
        roundedPanel1.setLayout(new java.awt.BorderLayout());

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel3.setText("Dashboard");
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 0));
        jLabel3.setPreferredSize(new java.awt.Dimension(190, 40));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        roundedPanel1.add(jLabel3, java.awt.BorderLayout.CENTER);

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(220, 220, 220));
        jLabel7.setText("Order Progress");
        jLabel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 16, 1, 1));
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButton1.setBackground(new java.awt.Color(20, 20, 20));
        jButton1.setForeground(new java.awt.Color(220, 220, 220));
        jButton1.setText("View Stock");
        jButton1.setBorderPainted(false);
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setBackground(new java.awt.Color(20, 20, 20));
        jButton2.setForeground(new java.awt.Color(220, 220, 220));
        jButton2.setText("Add Clothes");
        jButton2.setBorderPainted(false);
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jButton3.setBackground(new java.awt.Color(20, 20, 20));
        jButton3.setForeground(new java.awt.Color(220, 220, 220));
        jButton3.setText("Order Clothes");
        jButton3.setBorderPainted(false);
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton4.setBackground(new java.awt.Color(20, 20, 20));
        jButton4.setForeground(new java.awt.Color(220, 220, 220));
        jButton4.setText("Order Progress");
        jButton4.setBorderPainted(false);
        jButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jButton4.addActionListener(this::jButton4ActionPerformed);

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(sidebarPanelLayout.createSequentialGroup()
                                .addGap(196, 196, 196)
                                .addComponent(jLabel7))
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(49, 49, 49)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jButton1)
                .addGap(21, 21, 21)
                .addComponent(jButton2)
                .addGap(21, 21, 21)
                .addComponent(jButton3)
                .addGap(21, 21, 21)
                .addComponent(jButton4)
                .addGap(223, 223, 223)
                .addComponent(jLabel7)
                .addContainerGap(261, Short.MAX_VALUE))
        );

        getContentPane().add(sidebarPanel, java.awt.BorderLayout.LINE_START);

        mainContentPanel.setBackground(new java.awt.Color(251, 250, 245));

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 26)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(30, 30, 30));
        jLabel9.setText("Dashboard");

        roundedPanel2.setBackground(new java.awt.Color(238, 198, 67));
        roundedPanel2.setBorderColor(new java.awt.Color(238, 198, 67));
        roundedPanel2.setCornerRadius(10);
        roundedPanel2.setPreferredSize(new java.awt.Dimension(150, 40));
        roundedPanel2.setLayout(new java.awt.BorderLayout());

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("+ Add Clothes");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });
        roundedPanel2.add(jLabel10, java.awt.BorderLayout.CENTER);

        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(990, 130));
        jPanel1.setLayout(new java.awt.GridLayout(1, 3, 20, 0));

        roundedPanel4.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 22, 18, 22));
        roundedPanel4.setCornerRadius(14);
        roundedPanel4.setLayout(new javax.swing.BoxLayout(roundedPanel4, javax.swing.BoxLayout.Y_AXIS));

        jLabel11.setBackground(new java.awt.Color(115, 115, 115));
        jLabel11.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(115, 115, 115));
        jLabel11.setText("Total Items");
        roundedPanel4.add(jLabel11);

        lblTotalItems.setFont(new java.awt.Font("SansSerif", 1, 28)); // NOI18N
        lblTotalItems.setForeground(new java.awt.Color(30, 30, 30));
        lblTotalItems.setText("1,248");
        lblTotalItems.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 0, 0));
        roundedPanel4.add(lblTotalItems);

        jLabel13.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(115, 115, 115));
        jLabel13.setText("Across your collection");
        roundedPanel4.add(jLabel13);

        jPanel1.add(roundedPanel4);

        roundedPanel5.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 22, 18, 22));
        roundedPanel5.setCornerRadius(14);
        roundedPanel5.setLayout(new javax.swing.BoxLayout(roundedPanel5, javax.swing.BoxLayout.Y_AXIS));

        jLabel12.setBackground(new java.awt.Color(115, 115, 115));
        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(115, 115, 115));
        jLabel12.setText("Low Stock");
        roundedPanel5.add(jLabel12);

        lblLowStock.setFont(new java.awt.Font("SansSerif", 1, 28)); // NOI18N
        lblLowStock.setForeground(new java.awt.Color(30, 30, 30));
        lblLowStock.setText("12");
        lblLowStock.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 0, 0));
        roundedPanel5.add(lblLowStock);

        jLabel14.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(115, 115, 115));
        jLabel14.setText("Items need replenishing");
        roundedPanel5.add(jLabel14);

        jPanel1.add(roundedPanel5);

        roundedPanel3.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 22, 18, 22));
        roundedPanel3.setCornerRadius(14);
        roundedPanel3.setLayout(new javax.swing.BoxLayout(roundedPanel3, javax.swing.BoxLayout.Y_AXIS));

        jLabel15.setBackground(new java.awt.Color(115, 115, 115));
        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(115, 115, 115));
        jLabel15.setText("Pending Orders");
        roundedPanel3.add(jLabel15);

        lblPendingOrders.setFont(new java.awt.Font("SansSerif", 1, 28)); // NOI18N
        lblPendingOrders.setForeground(new java.awt.Color(30, 30, 30));
        lblPendingOrders.setText("8");
        lblPendingOrders.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 0, 0));
        roundedPanel3.add(lblPendingOrders);

        jLabel16.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(115, 115, 115));
        jLabel16.setText("Awaiting confirmation");
        roundedPanel3.add(jLabel16);

        jPanel1.add(roundedPanel3);

        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(990, 440));
        jPanel2.setLayout(new java.awt.GridLayout(1, 2, 20, 0));

        roundedPanel6.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));
        roundedPanel6.setCornerRadius(14);
        roundedPanel6.setLayout(new java.awt.BorderLayout(0, 16));

        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(30, 30, 30));
        jLabel17.setText("Low Stock Items");
        roundedPanel6.add(jLabel17, java.awt.BorderLayout.PAGE_START);

        tblLowStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Linen Shirt", " M", "3"},
                {"Tailored Trousers", "S", "2"},
                {"Silk Blouse", "M", "4"},
                {"Knit Cardigan", "L", "3"}
            },
            new String [] {
                "Item", "Size", "Qty Left"
            }
        ));
        tblLowStock.setGridColor(new java.awt.Color(238, 236, 230));
        tblLowStock.setRowHeight(42);
        tblLowStock.setSelectionBackground(new java.awt.Color(251, 250, 245));
        tblLowStock.setShowHorizontalLines(true);
        jScrollPane1.setViewportView(tblLowStock);

        roundedPanel6.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.add(roundedPanel6);

        roundedPanel7.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));
        roundedPanel7.setCornerRadius(14);
        roundedPanel7.setLayout(new java.awt.BorderLayout(0, 16));

        jLabel18.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel18.setText("Recent Orders");
        roundedPanel7.add(jLabel18, java.awt.BorderLayout.PAGE_START);

        tblRecentOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"#FRX-1048", "Maison Textiles", "Pending"},
                {"#FRX-1047", "Atelier Supply", "Processing"},
                {"#FRX-1046", "Studio Linen", "Shipped"},
                {"#FRX-1045", "Maison Textiles", "Delivered"}
            },
            new String [] {
                "Order #", "Supplier", "Status"
            }
        ));
        tblRecentOrders.setGridColor(new java.awt.Color(238, 236, 230));
        tblRecentOrders.setRowHeight(42);
        tblRecentOrders.setSelectionBackground(new java.awt.Color(251, 250, 245));
        tblRecentOrders.setShowHorizontalLines(true);
        jScrollPane2.setViewportView(tblRecentOrders);

        roundedPanel7.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel2.add(roundedPanel7);

        javax.swing.GroupLayout mainContentPanelLayout = new javax.swing.GroupLayout(mainContentPanel);
        mainContentPanel.setLayout(mainContentPanelLayout);
        mainContentPanelLayout.setHorizontalGroup(
            mainContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainContentPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(mainContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 871, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(mainContentPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(151, 151, 151))
        );
        mainContentPanelLayout.setVerticalGroup(
            mainContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainContentPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(mainContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );

        getContentPane().add(mainContentPanel, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 1296, 807);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        show(STOCK);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        show(ADD);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        show(ORDER);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        show(PROGRESS);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        show(DASHBOARD);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        show(ADD);
    }//GEN-LAST:event_jLabel10MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new DashboardFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblLowStock;
    private javax.swing.JLabel lblPendingOrders;
    private javax.swing.JLabel lblTotalItems;
    private javax.swing.JPanel mainContentPanel;
    private fabrix.RoundedPanel roundedPanel1;
    private fabrix.RoundedPanel roundedPanel2;
    private fabrix.RoundedPanel roundedPanel3;
    private fabrix.RoundedPanel roundedPanel4;
    private fabrix.RoundedPanel roundedPanel5;
    private fabrix.RoundedPanel roundedPanel6;
    private fabrix.RoundedPanel roundedPanel7;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tblLowStock;
    private javax.swing.JTable tblRecentOrders;
    // End of variables declaration//GEN-END:variables
}
