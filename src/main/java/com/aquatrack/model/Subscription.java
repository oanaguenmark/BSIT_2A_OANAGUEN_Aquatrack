package com.aquatrack.model;

/**
 * A regular refill plan: e.g. 3 Round gallons delivered
 * every Monday and Thursday.
 */
public class Subscription {

    private String subscriptionId;
    private Customer customer;
    private String gallonType;      // Round or Slim
    private String schedule;
    private int quantity;           // gallons per delivery
    private String status;          // Active, Paused, Cancelled

    public Subscription(
            String subscriptionId,
            Customer customer,
            String gallonType,
            String schedule,
            int quantity,
            String status) {

        this.subscriptionId = subscriptionId;
        this.customer = customer;
        this.gallonType = gallonType;
        this.schedule = schedule;
        this.quantity = quantity;
        this.status = status;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getGallonType() {
        return gallonType;
    }

    public String getSchedule() {
        return schedule;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setGallonType(String gallonType) {
        this.gallonType = gallonType;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayInfo() {

        System.out.println("Subscription ID : " + subscriptionId);
        System.out.println("Customer        : " + customer.getName());
        System.out.println("Gallon Type     : " + gallonType);
        System.out.println("Schedule        : " + schedule);
        System.out.println("Gallons/Delivery: " + quantity);
        System.out.println("Status          : " + status);
    }
}
