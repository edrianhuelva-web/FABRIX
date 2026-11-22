/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fabrix_Model;

/**
 *
 * @author edria
 */
public class FABRIX_Product {
    
   private String stockUnit;
    private String prodName;
    private String prodCategory;
    private String size;
    private String color;
    private double price;
    private int prodQty;

    // Constructor
    public FABRIX_Product(String sku, String name, String category,
                   String size, String color,
                   double price, int quantity) {

        this.stockUnit = stockUnit;
        this.prodName = name;
        this.prodCategory = category;
        this.size = size;
        this.color = color;
        this.price = price;
        this.prodQty = quantity;
    }

    // Getters
    public String getStockUnit() {
        return stockUnit;
    }

    public String getName() {
        return prodName;
    }

    public String getCategory() {
        return prodCategory;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public double getPrice() {
        return price;
    }

    public int getQty() {
        return prodQty;
    }

    // Setters
    public void setName(String name) {
        this.prodName = name;
    }

    public void setCategory(String category) {
        this.prodCategory = category;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.prodQty = quantity;
    }

    // Calculate inventory value of this product
    public double getInventoryValue() {
        return price * prodQty;
    }
}
