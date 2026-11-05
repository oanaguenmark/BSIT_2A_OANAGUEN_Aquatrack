package com.aquatrack.model;

/**
 * A sale. Walk-in orders are completed on the spot;
 * Delivery orders go Pending -> Out for Delivery -> Completed.
 * customer is null for anonymous walk-in buyers.
 */
public class Order {

    private String orderId;
    private Customer customer;
    private Product product;
    private int quantity;
    private String orderType;       // Walk-in or Delivery
    private String orderDate;
    private String address;
    private double total;
    private String paymentStatus;   // Paid or Unpaid
    private String status;          // Pending, Out for Delivery, Completed, Cancelled

    public Order(
            String orderId,
            Customer customer,
            Product product,
            int quantity,
            String orderType,
            String orderDate,
            String address,
            double total,
            String paymentStatus,
            String status) {

        this.orderId = orderId;
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.orderType = orderType;
        this.orderDate = orderDate;
        this.address = address;
        this.total = total;
        this.paymentStatus = paymentStatus;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCustomerName() {
        return customer == null ? "Walk-in Guest" : customer.getName();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getAddress() {
        return address;
    }

    public double getTotal() {
        return total;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayInfo() {

        System.out.println("Order ID : " + orderId);
        System.out.println("Customer : " + getCustomerName());
        System.out.println("Product  : " + product.getName()
                + " x" + quantity);
        System.out.println("Type     : " + orderType);
        System.out.println("Date     : " + orderDate);
        System.out.println("Address  : " + address);
        System.out.println("Total    : PHP "
                + String.format("%.2f", total));
        System.out.println("Payment  : " + paymentStatus);
        System.out.println("Status   : " + status);
    }
}
