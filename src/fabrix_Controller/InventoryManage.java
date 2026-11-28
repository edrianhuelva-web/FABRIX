package fabrix_Controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import fabrix_Model.FABRIX_Product;
import fabrix_Database.FileHandling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryManage {
    private final List<FABRIX_Product> products;

    // Constructor
    public InventoryManage() throws IOException {

        products = new ArrayList<>();

        loadProducts();
    }

    // Load products from CSV
    public final void loadProducts() throws IOException {

        products.clear();

        List<String[]> records = FileHandling.readCSV();

        // Skip CSV header
        for (int i = 1; i < records.size(); i++) {

            String[] row = records.get(i);

            if (row.length < 7) {
                continue;
            }

            try {

                FABRIX_Product product = new FABRIX_Product(
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    Double.parseDouble(row[5]),
                    Integer.parseInt(row[6])
                );

                products.add(product);

            } catch (NumberFormatException e) {

                System.err.println(
                    "Invalid product information on row " + (i + 1)
                );
            }
        }
    }

    // Get all products
    public List<FABRIX_Product> getAllProducts() {

        return Collections.unmodifiableList(products);
    }

    // Find product using SKU
    public FABRIX_Product findBySku(String sku) {

        if (sku == null) {
            return null;
        }

        for (FABRIX_Product p : products) {

            if (p.getStockUnit().equalsIgnoreCase(sku)) {
                return p;
            }
        }

        return null;
    }

    // Search products
    public List<FABRIX_Product> search(String keyword) {

        String q = keyword == null
                ? ""
                : keyword.trim().toLowerCase();

        List<FABRIX_Product> result = new ArrayList<>();

        for (FABRIX_Product p : products) {

            if (p.getStockUnit().toLowerCase().contains(q)
                || p.getName().toLowerCase().contains(q)
                || p.getCategory().toLowerCase().contains(q)
                || p.getColor().toLowerCase().contains(q)
                || p.getSize().toLowerCase().contains(q)) {

                result.add(p);
            }
        }

        return result;
    }

    // Add product
    public void addProduct(FABRIX_Product p) throws IOException {

        validate(p);

        if (findBySku(p.getStockUnit()) != null) {

            throw new IllegalArgumentException(
                "SKU already exists."
            );
        }

        products.add(p);

        save();
    }

    // Update product
    public void updateProduct(FABRIX_Product updated)
            throws IOException {

        validate(updated);

        for (int i = 0; i < products.size(); i++) {

            if (products.get(i).getStockUnit()
                    .equalsIgnoreCase(updated.getStockUnit())) {

                products.set(i, updated);

                save();

                return;
            }
        }

        throw new IllegalArgumentException(
            "Product SKU was not found."
        );
    }

    // Delete product
    public void deleteProduct(String sku) throws IOException {

        FABRIX_Product p = findBySku(sku);

        if (p == null) {

            throw new IllegalArgumentException(
                "Product SKU was not found."
            );
        }

        products.remove(p);

        save();
    }

    // Adjust stock
    public void adjustStock(String sku, int change)
            throws IOException {

        FABRIX_Product p = findBySku(sku);

        if (p == null) {

            throw new IllegalArgumentException(
                "Product SKU was not found."
            );
        }

        if (p.getQty() + change < 0) {

            throw new IllegalArgumentException(
                "Insufficient stock."
            );
        }

        p.setQuantity(p.getQty() + change);

        save();
    }

    // Get total stock units
    public int getTotalUnits() {

        int total = 0;

        for (FABRIX_Product p : products) {
            total += p.getQty();
        }

        return total;
    }

    // Get low-stock count
    public int getLowStockCount(int threshold) {

        int n = 0;

        for (FABRIX_Product p : products) {

            if (p.getQty() > 0 && p.getQty() <= threshold) {
                n++;
            }
        }

        return n;
    }

    // Get out-of-stock count
    public int getOutOfStockCount() {

        int n = 0;

        for (FABRIX_Product p : products) {

            if (p.getQty() == 0) {
                n++;
            }
        }

        return n;
    }

    // Get low-stock products
    public List<FABRIX_Product> getLowStockProducts(int threshold) {

        List<FABRIX_Product> result = new ArrayList<>();

        for (FABRIX_Product p : products) {

            if (p.getQty() <= threshold) {
                result.add(p);
            }
        }

        return result;
    }

    // Save products to CSV
    public void save() throws IOException {

        List<String[]> records = new ArrayList<>();

        records.add(new String[]{
            "StockUnit",
            "ProductName",
            "Category",
            "Size",
            "Color",
            "Price",
            "Quantity"
        });

        for (FABRIX_Product product : products) {

            records.add(new String[]{
                product.getStockUnit(),
                product.getName(),
                product.getCategory(),
                product.getSize(),
                product.getColor(),
                String.valueOf(product.getPrice()),
                String.valueOf(product.getQty())
            });
        }

        FileHandling.writeCSV(records);
    }

    // Validate product information
    private void validate(FABRIX_Product p) {

        if (p == null) {

            throw new IllegalArgumentException(
                "Product is required."
            );
        }

        if (blank(p.getStockUnit())
            || blank(p.getName())
            || blank(p.getCategory())
            || blank(p.getSize())
            || blank(p.getColor())) {

            throw new IllegalArgumentException(
                "Complete all product fields."
            );
        }

        if (!Double.isFinite(p.getPrice())
            || p.getPrice() < 0
            || p.getQty() < 0) {

            throw new IllegalArgumentException(
                "Price and stock must be valid non-negative values."
            );
        }
    }

    // Check empty strings
    private boolean blank(String s) {

        return s == null || s.trim().isEmpty();
    }
}
