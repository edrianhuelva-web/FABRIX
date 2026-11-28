/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fabrix_Controller;

import fabrix_Model.FABRIX_Product;

public class InventoryTest {
    public static void main(String[] args) {

        try {

            // Create InventoryManage object
            InventoryManage inventory = new InventoryManage();

            // Sample product information
            FABRIX_Product product = new FABRIX_Product(
                "FRX-TEST-001",
                "Oversized Shirt",
                "Tops",
                "M",
                "Black",
                499.00,
                20
            );

            // Check if the test product already exists
            if (inventory.findBySku("FRX-TEST-001") != null) {

                // Remove the previous test product
                inventory.deleteProduct("FRX-TEST-001");

            }

            // Add product
            inventory.addProduct(product);

            System.out.println("PRODUCT ADDED SUCCESSFULLY!");

            // Display all products
            System.out.println("\nPRODUCT INVENTORY:");

            for (FABRIX_Product p : inventory.getAllProducts()) {

                System.out.println(
                    p.getStockUnit() + " | "
                    + p.getName() + " | "
                    + p.getCategory() + " | "
                    + p.getSize() + " | "
                    + p.getColor() + " | "
                    + p.getPrice() + " | "
                    + p.getQty()
                );

            }

            // Test stock adjustment
            inventory.adjustStock("FRX-TEST-001", 5);

            System.out.println("\nUPDATED STOCK: "
                + inventory.findBySku("FRX-TEST-001").getQty());

            // Test product searching
            System.out.println("\nSEARCH RESULTS:");

            for (FABRIX_Product p : inventory.search("Oversized")) {

                System.out.println(p.getName());

            }

            // Display total stock units
            System.out.println("\nTOTAL STOCK UNITS: "
                + inventory.getTotalUnits());

            System.out.println("\nINVENTORY TEST COMPLETED!");

        } catch (Exception e) {

            System.out.println("ERROR: " + e.getMessage());

            e.printStackTrace();

        }

    }
}
