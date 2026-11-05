package com.aquatrack.ui;

import com.aquatrack.dao.UserDao;
import com.aquatrack.model.Customer;
import com.aquatrack.model.User;

import java.util.Scanner;

/** Menu for customers. */
public class CustomerMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final StationConsole console;

    public CustomerMenu(UserDao userDao, Scanner scanner, StationConsole console) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.console = console;
    }

    /** Runs until the customer logs out. */
    public void show(User user) {
        Customer customer = console.findOrCreateCustomer(user);

        while (true) {
            System.out.println("\n==========================================");
            System.out.println("             CUSTOMER MENU");
            System.out.println("==========================================");
            System.out.println("Welcome, " + customer.getName() + "!");
            System.out.println("1. Order Water for Delivery");
            System.out.println("2. My Orders");
            System.out.println("3. Cancel a Pending Order");
            System.out.println("4. My Subscription");
            System.out.println("5. My Gallons");
            System.out.println("6. My Balance");
            System.out.println("7. Logout");

            switch (console.readInt("Choose: ")) {
                case 1: console.createDeliveryOrder(customer); break;
                case 2: console.myOrders(customer); break;
                case 3: console.cancelMyOrder(customer); break;
                case 4: console.mySubscription(customer); break;
                case 5: console.myGallons(customer); break;
                case 6: console.myBalance(customer); break;
                case 7:
                    System.out.println("Logged out. Goodbye, " + customer.getName() + "!");
                    return;
                default: System.out.println("Invalid choice.");
            }
        }
    }
}
