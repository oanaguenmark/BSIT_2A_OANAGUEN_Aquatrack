package com.aquatrack.model;

/**
 * Something the station sells. Refill products are services
 * (no stock tracked); other products (new gallons, caps,
 * bottled water) have a stock count.
 */
public class Product {

    private String productId;
    private String name;
    private double price;
    private int stock;
    private boolean refill;

    public Product(
            String productId,
            String name,
            double price,
            int stock,
            boolean refill) {

        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.refill = refill;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public boolean isRefill() {
        return refill;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean hasStock(int quantity) {
        return refill || stock >= quantity;
    }

    public void reduceStock(int quantity) {
        if (!refill) {
            stock -= quantity;
        }
    }

    public void addStock(int quantity) {
        if (!refill) {
            stock += quantity;
        }
    }

    public String stockText() {
        return refill ? "N/A (refill service)" : String.valueOf(stock);
    }

    public String toRow() {
        return String.format("%-6s %-34s PHP %8.2f   Stock: %s",
                productId, name, price, stockText());
    }

    public void displayInfo() {

        System.out.println("Product ID : " + productId);
        System.out.println("Name       : " + name);
        System.out.println("Price      : PHP "
                + String.format("%.2f", price));
        System.out.println("Stock      : " + stockText());
    }
}
