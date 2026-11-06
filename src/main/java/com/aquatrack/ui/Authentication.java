package com.aquatrack.ui;

import com.aquatrack.dao.ActivityLogDao;
import com.aquatrack.dao.UserDao;
import com.aquatrack.model.Role;
import com.aquatrack.model.User;
import com.aquatrack.service.RefillStationSystem;

import java.util.Optional;
import java.util.Scanner;

/** Start screen: register, login, and routing to the role-based menus. No SQL here. */
public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;
    private final ActivityLogDao logDao;
    private final StationConsole console;
    private final AdminMenu adminMenu;
    private final StaffMenu staffMenu;
    private final CustomerMenu customerMenu;

    public Authentication(UserDao userDao, ActivityLogDao logDao, Scanner scanner,
                          RefillStationSystem system) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.scanner = scanner;
        // The same DAOs, Scanner and StationConsole are shared with every menu.
        this.console = new StationConsole(system, scanner, logDao);
        this.adminMenu = new AdminMenu(userDao, logDao, scanner, console);
        this.staffMenu = new StaffMenu(userDao, scanner, console);
        this.customerMenu = new CustomerMenu(userDao, scanner, console);
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== AQUATRACK =====");
            System.out.println("Purified Water Refilling Station Management System");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "0" -> {
                    System.out.println("Thank you for using AquaTrack. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Register ---");
        String username = prompt("Username: ");
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("Username already taken.");
            return;
        }
        String password = prompt("Password: ");
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }
        String fullName = prompt("Full name: ");
        if (fullName.isEmpty()) {
            System.out.println("Full name cannot be empty.");
            return;
        }

        System.out.println("Select role:");
        System.out.println("1. ADMIN");
        System.out.println("2. STAFF");
        System.out.println("3. CUSTOMER");
        Role role = switch (prompt("Role: ")) {
            case "1" -> Role.ADMIN;
            case "2" -> Role.STAFF;
            case "3" -> Role.CUSTOMER;
            default -> null;
        };
        if (role == null) {
            System.out.println("Invalid role. Registration cancelled.");
            return;
        }

        if (userDao.register(username, password, fullName, role)) {
            System.out.println("Registration successful! Role: " + role + " | Status: PENDING");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        Optional<User> result = userDao.login(username, password);
        if (result.isEmpty()) {
            System.out.println("Invalid username or password.");
            return;
        }
        User user = result.get();
        System.out.println("Login successful. Welcome, " + user.fullName() + "!");
        logDao.logAction(user.userId(), "LOGIN",
                "User " + user.username() + " (ID: " + user.userId() + ") logged in.");
        console.setCurrentUser(user);

        switch (user.role()) {
            case ADMIN -> adminMenu.show(user);
            case STAFF -> staffMenu.show(user);
            case CUSTOMER -> customerMenu.show(user);
        }

        logout(user);
    }

    /** Called when a role menu returns (the user chose Logout). */
    private void logout(User user) {
        logDao.logAction(user.userId(), "LOGOUT",
                "User " + user.username() + " (ID: " + user.userId() + ") logged out.");
        console.setCurrentUser(null);
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }
}
