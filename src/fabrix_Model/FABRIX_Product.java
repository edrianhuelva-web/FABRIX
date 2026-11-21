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
    
    private String productID;
    private String productName;
    private String category;
    private String size;
    private String color;
    private double price;
    private int quantity;
    private int reorderLevel;

    public FABRIX_Product(String productID, String productName,
                   String category, String size, String color,
                   double price, int quantity, int reorderLevel) {

        this.productID = productID;
        this.productName = productName;
        this.category = category;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }
}
