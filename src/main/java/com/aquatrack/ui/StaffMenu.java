package com.aquatrack.ui;

import com.aquatrack.dao.UserDao;
import com.aquatrack.model.User;

import java.util.Scanner;

/** Menu for the Station Attendant / Delivery Staff. */
public class StaffMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final StationConsole console;

    public StaffMenu(UserDao userDao, Scanner scanner, StationConsole console) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.console = console;
    }

    /** Runs until the staff member logs out. */
    public void show(User user) {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("              STAFF MENU");
            System.out.println("==========================================");
            System.out.println("Logged in as: " + user.fullName());
            System.out.println("1. New Walk-in Sale");
            System.out.println("2. New Delivery Order");
            System.out.println("3. View Orders");
            System.out.println("4. Update Order Status");
            System.out.println("5. Mark Order as Paid");
            System.out.println("6. View Gallons");
            System.out.println("7. Update Gallon Status");
            System.out.println("8. Receive Returned Gallon");
            System.out.println("9. Water Quality Logs");
            System.out.println("10. View Products & Stock");
            System.out.println("11. Logout");

            switch (console.readInt("Choose: ")) {
                case 1: console.walkInSale(); break;
                case 2: console.staffDeliveryOrder(); break;
                case 3: console.readOrders(); break;
                case 4: console.updateOrderStatus(); break;
                case 5: console.markPaid(); break;
                case 6: console.readGallons(); break;
                case 7: console.updateGallon(); break;
                case 8: console.receiveGallon(); break;
                case 9: console.qualityManagement(user.fullName()); break;
                case 10: console.readProducts(); break;
                case 11:
                    System.out.println("Logged out. Goodbye, " + user.fullName() + "!");
                    return;
                default: System.out.println("Invalid choice.");
            }
        }
    }
}
