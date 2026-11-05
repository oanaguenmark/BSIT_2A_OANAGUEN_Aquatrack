package com.aquatrack.ui;

import com.aquatrack.dao.UserDao;
import com.aquatrack.model.User;

import java.util.List;
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
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Dashboard");
            System.out.println("5. Customer Management");
            System.out.println("6. Products & Stock");
            System.out.println("7. Order Management");
            System.out.println("8. Subscription Management");
            System.out.println("9. Gallon Management");
            System.out.println("10. Water Quality Logs");
            System.out.println("11. Reports");
            System.out.println("12. Logout");

            switch (console.readInt("Choose: ")) {
                case 1: viewAllUsers(); break;
                case 2: viewPendingUsers(); break;
                case 3: approveUser(); break;
                case 4: console.dashboard(); break;
                case 5: console.customerManagement(); break;
                case 6: console.productManagement(); break;
                case 7: console.orderManagement(); break;
                case 8: console.subscriptionManagement(); break;
                case 9: console.gallonManagement(); break;
                case 10: console.qualityManagement("System Administrator"); break;
                case 11: console.reports(); break;
                case 12:
                    System.out.println("Logged out. Goodbye, " + user.fullName() + "!");
                    return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------------------------------------------------------
    // USER ACCOUNTS
    // ---------------------------------------------------------------

    private void viewAllUsers() {
        System.out.println("\n========== ALL SYSTEM USERS ==========");
        printUserTable(userDao.getAllUsers());
    }

    private void viewPendingUsers() {
        System.out.println("\n========== PENDING REGISTRATIONS ==========");
        printUserTable(userDao.getPendingUsers());
    }

    private void approveUser() {
        System.out.println("\n========== APPROVE USER ACCOUNT ==========");

        List<User> pending = userDao.getPendingUsers();
        printUserTable(pending);

        if (pending.isEmpty()) {
            return;
        }

        while (true) {
            int id = console.readInt("Enter User ID to approve (0 to cancel): ");

            if (id == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            User target = pending.stream()
                    .filter(u -> u.userId() == id)
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                System.out.println("ID " + id + " is not in the pending list. Try again.");
                continue;
            }

            if (userDao.approveUser(id)) {
                System.out.println("Success! " + target.username() + " (" + target.role()
                        + ") has been APPROVED.");
            } else {
                System.out.println("Approval failed. Please try again.");
            }
            return;
        }
    }

    /** Prints any list of users as an aligned table. */
    private void printUserTable(List<User> users) {
        String border = "+-------+-----------------+--------------+------------+";
        String row = "| %-5s | %-15s | %-12s | %-10s |%n";

        System.out.println(border);
        System.out.printf(row, "ID", "Username", "Role", "Status");
        System.out.println(border);

        if (users.isEmpty()) {
            System.out.printf("| %-51s |%n", "No users found.");
        }
        for (User u : users) {
            System.out.printf(row, u.userId(), cut(u.username(), 15),
                    u.role(), u.status());
        }
        System.out.println(border);
        System.out.println("Total: " + users.size());
    }

    private String cut(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + "~";
    }
}