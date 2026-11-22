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
    private String prodCat;
    private String size;
    private String color;
    private double price;
    private int prodQty;

    // Constructor
    public FABRIX_Product(String stockUnit, String prodName, String prodCat,
                   String size, String color,
                   double price, int prodQty) {

        this.stockUnit = stockUnit;
        this.prodName = prodName;
        this.prodCat = prodCat;
        this.size = size;
        this.color = color;
        this.price = price;
        this.prodQty = prodQty;
    }

    public String getStockUnit() {
        return stockUnit;
    }

    public String getName() {
        return prodName;
    }

    public String getCategory() {
        return prodCat;
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

    public void setCat(String category) {
        this.prodCat = category;
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
