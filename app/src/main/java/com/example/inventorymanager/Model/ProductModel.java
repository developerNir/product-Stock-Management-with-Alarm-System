
package com.example.inventorymanager.Model;

public class ProductModel {
    private int id;
    private String name, description, stock;

    private byte[] image;
    private double price;
    private int quantity;
    private int subCatId;

    public ProductModel(int id, String name, byte[] image, String description, double price, int quantity, String stock, int subCatId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.subCatId = subCatId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public byte[] getImage() { return image; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStock() { return stock; }
    public int getSubCatId() { return subCatId; }
}
