package com.aquatrack.ui;

import com.aquatrack.dao.ActivityLogDao;
import com.aquatrack.dao.UserDao;
import com.aquatrack.model.ActivityLog;
import com.aquatrack.model.Role;
import com.aquatrack.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Menu for the Station Owner / Administrator. */
public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final Scanner scanner;
    private final StationConsole console;

    public AdminMenu(UserDao userDao, ActivityLogDao logDao, Scanner scanner,
                     StationConsole console) {
        this.userDao = userDao;
        this.logDao = logDao;
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
            System.out.println("4. Update User Role / Status");
            System.out.println("5. View System Audit Logs");
            System.out.println("6. Dashboard");
            System.out.println("7. Customer Management");
            System.out.println("8. Products & Stock");
            System.out.println("9. Order Management");
            System.out.println("10. Subscription Management");
            System.out.println("11. Gallon Management");
            System.out.println("12. Water Quality Logs");
            System.out.println("13. Reports");
            System.out.println("14. Logout");

            switch (readIntInput("Choose: ")) {
                case 1: viewAllUsers(); break;
                case 2: viewPendingUsers(); break;
                case 3: approveUser(user); break;
                case 4: updateUser(user); break;
                case 5: auditLogMenu(user); break;
                case 6: console.dashboard(); break;
                case 7: console.customerManagement(); break;
                case 8: console.productManagement(); break;
                case 9: console.orderManagement(); break;
                case 10: console.subscriptionManagement(); break;
                case 11: console.gallonManagement(); break;
                case 12: console.qualityManagement("System Administrator"); break;
                case 13: console.reports(); break;
                case 14:
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

    private void approveUser(User admin) {
        System.out.println("\n========== APPROVE USER ACCOUNT ==========");

        List<User> pending = userDao.getPendingUsers();
        printUserTable(pending);

        if (pending.isEmpty()) {
            return;
        }

        while (true) {
            int id = readIntInput("Enter User ID to approve (0 to cancel): ");

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
                logDao.logAction(admin.userId(), "USER_APPROVED",
                        "Admin " + admin.userId() + " approved account for User ID " + id);
            } else {
                System.out.println("Approval failed. Please try again.");
            }
            return;
        }
    }

    /** Changes another user's role and status. Enter 0 at any step to cancel. */
    private void updateUser(User currentAdmin) {
        System.out.println("\n========== UPDATE USER ROLE / STATUS ==========");

        List<User> users = userDao.getAllUsers();
        printUserTable(users);

        User target;
        while (true) {
            int id = readIntInput("Enter User ID to update (0 to cancel): ");

            if (id == 0) {
                System.out.println("Update cancelled.");
                return;
            }
            if (id == currentAdmin.userId()) {
                System.out.println("WARNING: Modifying the currently active admin account is restricted.");
                continue;
            }

            target = users.stream().filter(u -> u.userId() == id).findFirst().orElse(null);
            if (target == null) {
                System.out.println("ID " + id + " does not exist. Try again.");
                continue;
            }
            break;
        }

        System.out.println("\nUpdating: " + target.username()
                + " (current role: " + target.role() + ", status: " + target.status() + ")");

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(target.userId(), newRole, newStatus)) {
            System.out.println("Success! " + target.username() + " is now " + newRole
                    + " with status " + newStatus + ".");
            logDao.logAction(currentAdmin.userId(), "USER_UPDATED",
                    "Admin " + currentAdmin.userId() + " changed User " + target.userId()
                    + " to role " + newRole + ", status " + newStatus);
        } else {
            System.out.println("Update failed. Please try again.");
        }
    }

    // ---------------------------------------------------------------
    // INPUT VALIDATION
    // ---------------------------------------------------------------

    /** Keeps asking until the user types a whole number. */
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /** Returns a valid role name, or null if the admin entered 0 to cancel. */
    private String readValidRole() {
        while (true) {
            System.out.print("New role (ADMIN / STAFF / CUSTOMER, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("0")) {
                return null;
            }
            for (Role role : Role.values()) {
                if (role.name().equals(input)) {
                    return input;
                }
            }
            System.out.println("Invalid role. Please enter ADMIN, STAFF or CUSTOMER.");
        }
    }

    /** Returns a valid status, or null if the admin entered 0 to cancel. */
    private String readValidStatus() {
        while (true) {
            System.out.print("New status (APPROVED / PENDING, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("0")) {
                return null;
            }
            if (input.equals("APPROVED") || input.equals("PENDING")) {
                return input;
            }
            System.out.println("Invalid status. Please enter APPROVED or PENDING.");
        }
    }

    // ---------------------------------------------------------------
    // AUDIT LOGS
    // ---------------------------------------------------------------

    private void auditLogMenu(User admin) {
        while (true) {
            System.out.println("\n========== SYSTEM AUDIT LOGS ==========");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Clear All Logs");
            System.out.println("0. Back");

            switch (readIntInput("Choose: ")) {
                case 1 -> printLogTable(logDao.getAllLogs());
                case 2 -> {
                    int id = readIntInput("Enter User ID (0 to cancel): ");
                    if (id == 0) {
                        System.out.println("Filter cancelled.");
                    } else {
                        printLogTable(logDao.getLogsByUserId(id));
                    }
                }
                case 3 -> clearLogs(admin);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    /** Deletes all logs after an explicit YES, then records that the clear happened. */
    private void clearLogs(User admin) {
        System.out.print("This permanently deletes ALL log entries. Type YES to confirm: ");
        String answer = scanner.nextLine().trim();

        if (!answer.equals("YES")) {
            System.out.println("Clear cancelled. No logs were deleted.");
            return;
        }

        int removed = logDao.clearAllLogs();
        System.out.println(removed + " log entries cleared.");
        logDao.logAction(admin.userId(), "LOGS_CLEARED",
                "Admin " + admin.userId() + " cleared " + removed + " log entries");
    }

    /** Prints logs as an aligned table; long descriptions wrap onto extra lines. */
    private void printLogTable(List<ActivityLog> logs) {
        String row = "%-5s | %-8s | %-18s | %-35s | %-20s%n";
        String line = "-".repeat(5) + "-+-" + "-".repeat(8) + "-+-" + "-".repeat(18)
                + "-+-" + "-".repeat(35) + "-+-" + "-".repeat(20);

        System.out.println();
        System.out.printf(row, "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println(line);

        if (logs.isEmpty()) {
            System.out.println("No log entries found.");
        }
        for (ActivityLog log : logs) {
            List<String> parts = wrap(log.description(), 35);
            System.out.printf(row, log.logId(), log.userId(), log.actionType(),
                    parts.get(0), log.timestamp());
            for (int i = 1; i < parts.size(); i++) {
                System.out.printf(row, "", "", "", parts.get(i), "");
            }
        }
        System.out.println(line);
        System.out.println("Total: " + logs.size());
    }

    /** Splits text into lines of at most 'width' characters, breaking at spaces. */
    private List<String> wrap(String text, int width) {
        List<String> lines = new ArrayList<>();
        String rest = text == null ? "" : text.trim();
        while (rest.length() > width) {
            int cut = rest.lastIndexOf(' ', width);
            if (cut <= 0) {
                cut = width;
            }
            lines.add(rest.substring(0, cut).trim());
            rest = rest.substring(cut).trim();
        }
        lines.add(rest);
        return lines;
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
