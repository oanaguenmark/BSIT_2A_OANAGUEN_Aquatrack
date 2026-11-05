package com.aquatrack.model;

import java.util.ArrayList;

public class Customer extends Person {

    private String customerId;
    private String address;
    private String contactNumber;
    private ArrayList<Gallon> gallons;

    public Customer(
            String customerId,
            String username,
            String password,
            String name,
            String address,
            String contactNumber) {

        super(username, password, name);

        this.customerId = customerId;
        this.address = address;
        this.contactNumber = contactNumber;
        this.gallons = new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public ArrayList<Gallon> getGallons() {
        return gallons;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void addGallon(Gallon gallon) {
        if (!gallons.contains(gallon)) {
            gallons.add(gallon);
        }
    }

    public void removeGallon(Gallon gallon) {
        gallons.remove(gallon);
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Customer");
    }

    public void displayInfo() {

        System.out.println("Customer ID  : " + customerId);
        System.out.println("Name         : " + name);
        System.out.println("Username     : " + username);
        System.out.println("Contact      : " + contactNumber);
        System.out.println("Address      : " + address);
        System.out.println("Gallons Held : " + gallons.size());
    }
}
