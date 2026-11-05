package com.aquatrack.ui;

import com.aquatrack.dao.UserDao;
import com.aquatrack.model.User;

import java.util.Scanner;

/** Menu for the Station Owner / Administrator. */
public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final StationConsole console;

    public AdminMenu(UserDao userDao, Scanner scanner, StationConsole console) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.console = console;
    }

    /** Runs until the admin logs out. */
    public void show(User user) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("              ADMIN MENU");
            System.out.println("==========================================");
            System.out.println("Logged in as: " + user.fullName());
            System.out.println("1. Dashboard");
            System.out.println("2. Customer Management");
            System.out.println("3. Products & Stock");
            System.out.println("4. Order Management");
            System.out.println("5. Subscription Management");
            System.out.println("6. Gallon Management");
            System.out.println("7. Water Quality Logs");
            System.out.println("8. Reports");
            System.out.println("9. Logout");

            switch (console.readInt("Choose: ")) {
                case 1: console.dashboard(); break;
                case 2: console.customerManagement(); break;
                case 3: console.productManagement(); break;
                case 4: console.orderManagement(); break;
                case 5: console.subscriptionManagement(); break;
                case 6: console.gallonManagement(); break;
                case 7: console.qualityManagement("System Administrator"); break;
                case 8: console.reports(); break;
                case 9:
                    System.out.println("Logged out. Goodbye, " + user.fullName() + "!");
                    return;
                default: System.out.println("Invalid choice.");
            }
        }
    }
}
