package com.aquatrack.ui;

import com.aquatrack.model.*;
import com.aquatrack.service.RefillStationSystem;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * All station operations (customers, products, orders, subscriptions, gallons,
 * water quality, reports) shared by the role menus. Data is still kept in memory
 * by RefillStationSystem; it will move to DAOs in a later deliverable.
 */
public class StationConsole {

    private final RefillStationSystem system;
    private final Scanner scanner;

    public StationConsole(RefillStationSystem system, Scanner scanner) {
        this.system = system;
        this.scanner = scanner;
    }

    public RefillStationSystem getSystem() {
        return system;
    }

    public void customerManagement() {

        while (true) {

            System.out.println("\n========== CUSTOMER MANAGEMENT ==========");
            System.out.println("1. Create Customer");
            System.out.println("2. Read Customers");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. Back");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1: createCustomer(); break;
                case 2: readCustomers(); break;
                case 3: updateCustomer(); break;
                case 4: deleteCustomer(); break;
                case 5: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    public void createCustomer() {

        System.out.println("\n========== CREATE CUSTOMER ==========");

        String username = readString("Username: ");

        if (username.isEmpty()
                || username.equalsIgnoreCase("admin")
                || username.equalsIgnoreCase("staff")
                || system.findCustomerByUsername(username) != null) {
            System.out.println("Username is empty or already taken.");
            return;
        }

        String password = readString("Password: ");
        String name = readString("Full Name: ");
        String address = readString("Delivery Address: ");
        String contact = readString("Contact Number: ");

        Customer customer = system.addCustomer(
                username, password, name, address, contact);

        System.out.println("\nCustomer created successfully!");
        System.out.println("Customer ID: " + customer.getCustomerId());
    }

    public void readCustomers() {

        System.out.println("\n========== CUSTOMERS ==========");

        if (system.getCustomers().isEmpty()) {
            System.out.println("No customers yet.");
            return;
        }

        for (Customer customer : system.getCustomers()) {
            System.out.println("--------------------------------");
            customer.displayInfo();
            System.out.println("Unpaid Balance: "
                    + money(system.getCustomerBalance(customer)));
        }
    }

    public void updateCustomer() {

        readCustomers();

        String id = readString("\nCustomer ID: ");
        Customer customer = system.findCustomer(id);

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.println("(Leave blank to keep current value)");
        String name = readString("New Name: ");
        String address = readString("New Address: ");
        String contact = readString("New Contact: ");

        if (!name.isEmpty()) customer.setName(name);
        if (!address.isEmpty()) customer.setAddress(address);
        if (!contact.isEmpty()) customer.setContactNumber(contact);

        System.out.println("\nCustomer updated successfully!");
    }

    public void deleteCustomer() {

        readCustomers();

        String id = readString("\nCustomer ID to delete: ");

        if (system.deleteCustomer(id)) {
            System.out.println("\nCustomer deleted successfully!");
        } else {
            System.out.println("\nCustomer not found.");
        }
    }

    public void productManagement() {

        while (true) {

            System.out.println("\n========== PRODUCTS & STOCK ==========");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Name / Price");
            System.out.println("4. Restock Product");
            System.out.println("5. Delete Product");
            System.out.println("6. Back");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1: createProduct(); break;
                case 2: readProducts(); break;
                case 3: updateProduct(); break;
                case 4: restockProduct(); break;
                case 5: deleteProduct(); break;
                case 6: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    public void createProduct() {

        System.out.println("\n========== ADD PRODUCT ==========");

        String name = readString("Product Name: ");
        double price = readDouble("Price (PHP): ");
        String type = readString("Is this a refill service? (y/n): ");

        boolean refill = type.equalsIgnoreCase("y");
        int stock = refill ? 0 : readInt("Starting Stock: ");

        Product p = system.addProduct(name, price, stock, refill);
        System.out.println("\nProduct added! ID: " + p.getProductId());
    }

    public void readProducts() {

        System.out.println("\n========== PRODUCTS ==========");

        for (Product p : system.getProducts()) {
            System.out.println(p.toRow());
        }

        System.out.println("\nDelivery fee: "
                + money(RefillStationSystem.DELIVERY_FEE)
                + " per delivery order");
    }

    public void updateProduct() {

        readProducts();

        Product p = system.findProduct(readString("\nProduct ID: "));

        if (p == null) {
            System.out.println("Product not found.");
            return;
        }

        String name = readString("New Name (blank = keep): ");
        String price = readString("New Price (blank = keep): ");

        if (!name.isEmpty()) p.setName(name);

        if (!price.isEmpty()) {
            try {
                p.setPrice(Double.parseDouble(price));
            } catch (NumberFormatException e) {
                System.out.println("Invalid price, price not changed.");
            }
        }

        System.out.println("\nProduct updated!");
    }

    public void restockProduct() {

        readProducts();

        Product p = system.findProduct(readString("\nProduct ID: "));

        if (p == null) {
            System.out.println("Product not found.");
            return;
        }

        if (p.isRefill()) {
            System.out.println("Refill services don't use stock.");
            return;
        }

        int qty = readInt("Quantity to add: ");

        if (qty <= 0) {
            System.out.println("Quantity must be more than 0.");
            return;
        }

        p.addStock(qty);
        System.out.println("\nNew stock: " + p.getStock());
    }

    public void deleteProduct() {

        readProducts();

        if (system.deleteProduct(readString("\nProduct ID to delete: "))) {
            System.out.println("\nProduct deleted!");
        } else {
            System.out.println("\nProduct not found.");
        }
    }

    public void orderManagement() {

        while (true) {

            System.out.println("\n========== ORDER MANAGEMENT ==========");
            System.out.println("1. New Walk-in Sale");
            System.out.println("2. New Delivery Order");
            System.out.println("3. View Orders");
            System.out.println("4. Update Order Status");
            System.out.println("5. Mark Order as Paid");
            System.out.println("6. Delete Order");
            System.out.println("7. Back");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1: walkInSale(); break;
                case 2: staffDeliveryOrder(); break;
                case 3: readOrders(); break;
                case 4: updateOrderStatus(); break;
                case 5: markPaid(); break;
                case 6: deleteOrder(); break;
                case 7: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    public void walkInSale() {

        System.out.println("\n========== WALK-IN SALE ==========");

        String id = readString("Customer ID (blank = guest): ");
        Customer customer = null;

        if (!id.isEmpty()) {
            customer = system.findCustomer(id);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }
        }

        Product product = chooseProduct();
        if (product == null) return;

        int qty = readInt("Quantity: ");
        boolean paid = readString("Paid now? (y/n): ").equalsIgnoreCase("y");

        Order order = system.placeOrder(customer, product, qty,
                "Walk-in", LocalDate.now().toString(), paid);

        printOrderResult(order);
    }

    public void staffDeliveryOrder() {

        System.out.println("\n========== NEW DELIVERY ORDER ==========");

        readCustomers();

        Customer customer = system.findCustomer(readString("\nCustomer ID: "));

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        createDeliveryOrder(customer);
    }

    public void createDeliveryOrder(Customer customer) {

        Product product = chooseProduct();
        if (product == null) return;

        int qty = readInt("Quantity: ");
        String date = readDate();

        // delivery is cash-on-delivery: unpaid until received
        Order order = system.placeOrder(customer, product, qty,
                "Delivery", date, false);

        printOrderResult(order);
    }

    public void printOrderResult(Order order) {

        if (order == null) {
            System.out.println("\nOrder failed: invalid quantity "
                    + "or not enough stock.");
            return;
        }

        System.out.println("\nOrder placed successfully!");
        System.out.println("--------------------------------");
        order.displayInfo();
    }

    public void readOrders() {

        System.out.println("\n========== ORDERS ==========");

        if (system.getOrders().isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }

        for (Order order : system.getOrders()) {
            System.out.println("--------------------------------");
            order.displayInfo();
        }
    }

    public void updateOrderStatus() {

        readOrders();

        Order order = system.findOrder(readString("\nOrder ID: "));

        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        String status = orderStatus();

        if (!system.updateOrderStatus(order, status)) {
            System.out.println("\nCompleted/Cancelled orders can't be changed.");
            return;
        }

        System.out.println("\nOrder status updated!");

        if (status.equals("Completed")
                && order.getPaymentStatus().equals("Unpaid")) {

            String paid = readString("Payment received now? (y/n): ");

            if (paid.equalsIgnoreCase("y")) {
                system.markOrderPaid(order);
                System.out.println("Marked as Paid.");
            }
        }
    }

    public void markPaid() {

        readOrders();

        Order order = system.findOrder(readString("\nOrder ID: "));

        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        system.markOrderPaid(order);
        System.out.println("\nOrder marked as Paid.");
    }

    public void deleteOrder() {

        readOrders();

        if (system.deleteOrder(readString("\nOrder ID to delete: "))) {
            System.out.println("\nOrder deleted!");
        } else {
            System.out.println("\nOrder not found.");
        }
    }

    public void subscriptionManagement() {

        while (true) {

            System.out.println("\n========== SUBSCRIPTION MANAGEMENT ==========");
            System.out.println("1. Create Subscription");
            System.out.println("2. Read Subscriptions");
            System.out.println("3. Update Subscription");
            System.out.println("4. Delete Subscription");
            System.out.println("5. Generate Delivery Order from Subscription");
            System.out.println("6. Back");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1: createSubscription(); break;
                case 2: readSubscriptions(); break;
                case 3: updateSubscription(); break;
                case 4: deleteSubscription(); break;
                case 5: generateFromSubscription(); break;
                case 6: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    public void createSubscription() {

        readCustomers();

        Customer customer = system.findCustomer(readString("\nCustomer ID: "));

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        String type = gallonType();
        String schedule = readString("Schedule (e.g. Every Monday): ");
        int qty = readInt("Gallons per delivery: ");

        if (qty <= 0) {
            System.out.println("Quantity must be more than 0.");
            return;
        }

        Subscription sub = system.addSubscription(
                customer, type, schedule, qty);

        System.out.println("\nSubscription created! ID: "
                + sub.getSubscriptionId());
    }

    public void readSubscriptions() {

        System.out.println("\n========== SUBSCRIPTIONS ==========");

        if (system.getSubscriptions().isEmpty()) {
            System.out.println("No subscriptions yet.");
            return;
        }

        for (Subscription sub : system.getSubscriptions()) {
            System.out.println("--------------------------------");
            sub.displayInfo();
        }
    }

    public void updateSubscription() {

        readSubscriptions();

        Subscription sub = system.findSubscription(
                readString("\nSubscription ID: "));

        if (sub == null) {
            System.out.println("Subscription not found.");
            return;
        }

        String schedule = readString("New Schedule (blank = keep): ");
        String qty = readString("New Gallons per delivery (blank = keep): ");

        if (!schedule.isEmpty()) sub.setSchedule(schedule);

        if (!qty.isEmpty()) {
            try {
                int q = Integer.parseInt(qty);
                if (q > 0) sub.setQuantity(q);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity, not changed.");
            }
        }

        System.out.println("\nChange status:");
        sub.setStatus(subscriptionStatus());

        System.out.println("\nSubscription updated!");
    }

    public void deleteSubscription() {

        readSubscriptions();

        if (system.deleteSubscription(
                readString("\nSubscription ID to delete: "))) {
            System.out.println("\nSubscription deleted!");
        } else {
            System.out.println("\nSubscription not found.");
        }
    }

    public void generateFromSubscription() {

        readSubscriptions();

        Subscription sub = system.findSubscription(
                readString("\nSubscription ID: "));

        if (sub == null) {
            System.out.println("Subscription not found.");
            return;
        }

        Order order = system.generateOrderFromSubscription(
                sub, readDate());

        if (order == null) {
            System.out.println("\nCould not create order "
                    + "(subscription not Active, or no matching refill product).");
            return;
        }

        printOrderResult(order);
    }

    public void gallonManagement() {

        while (true) {

            System.out.println("\n========== GALLON MANAGEMENT ==========");
            System.out.println("1. Add Gallon");
            System.out.println("2. View Gallons");
            System.out.println("3. Update Gallon Status");
            System.out.println("4. Assign Gallon to Customer");
            System.out.println("5. Receive Returned Gallon");
            System.out.println("6. Delete Gallon");
            System.out.println("7. Back");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1: createGallon(); break;
                case 2: readGallons(); break;
                case 3: updateGallon(); break;
                case 4: assignGallon(); break;
                case 5: receiveGallon(); break;
                case 6: deleteGallon(); break;
                case 7: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    public void createGallon() {

        String type = gallonType();
        String location = readString("Location (e.g. Station): ");

        Gallon g = system.addGallon(
                type, location.isEmpty() ? "Station" : location, "Ready");

        System.out.println("\nGallon added! ID: " + g.getGallonId());
    }

    public void readGallons() {

        System.out.println("\n========== GALLONS ==========");

        if (system.getGallons().isEmpty()) {
            System.out.println("No gallons registered.");
            return;
        }

        for (Gallon g : system.getGallons()) {
            System.out.println("--------------------------------");
            g.displayInfo();
        }
    }

    public void updateGallon() {

        readGallons();

        Gallon g = system.findGallon(readString("\nGallon ID: "));

        if (g == null) {
            System.out.println("Gallon not found.");
            return;
        }

        String status = gallonStatus();

        // a gallon marked as being at the station can't still be "held"
        if (!status.equals("With Customer")) {
            Customer holder = system.findGallonHolder(g);
            if (holder != null) {
                holder.removeGallon(g);
            }
        }

        g.setStatus(status);

        if (status.equals("Out for Delivery")) {
            g.setCurrentLocation("Delivery Vehicle");
        } else if (!status.equals("With Customer")) {
            g.setCurrentLocation("Station");
        }

        System.out.println("\nGallon status updated!");
    }

    public void assignGallon() {

        readGallons();

        Gallon g = system.findGallon(readString("\nGallon ID: "));

        if (g == null) {
            System.out.println("Gallon not found.");
            return;
        }

        readCustomers();

        Customer c = system.findCustomer(readString("\nCustomer ID: "));

        if (c == null) {
            System.out.println("Customer not found.");
            return;
        }

        system.assignGallon(g, c);
        System.out.println("\n" + g.getGallonId() + " assigned to " + c.getName());
    }

    public void receiveGallon() {

        readGallons();

        Gallon g = system.findGallon(readString("\nReturned Gallon ID: "));

        if (g == null) {
            System.out.println("Gallon not found.");
            return;
        }

        system.returnGallon(g);
        System.out.println("\nGallon received. Status: Needs Refill");
    }

    public void deleteGallon() {

        readGallons();

        if (system.deleteGallon(readString("\nGallon ID to delete: "))) {
            System.out.println("\nGallon deleted!");
        } else {
            System.out.println("\nGallon not found.");
        }
    }

    public void qualityManagement(String checkedBy) {

        while (true) {

            System.out.println("\n========== WATER QUALITY LOGS ==========");
            System.out.println("1. Record Today's Reading");
            System.out.println("2. View Logs");
            System.out.println("3. Back");

            int choice = readInt("Choose: ");

            switch (choice) {
                case 1: recordQuality(checkedBy); break;
                case 2: readQuality(); break;
                case 3: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    public void recordQuality(String checkedBy) {

        System.out.println("\n========== RECORD WATER QUALITY ==========");
        System.out.println("Pass limits: TDS <= " + WaterQualityLog.MAX_TDS
                + " ppm, pH " + WaterQualityLog.MIN_PH
                + " - " + WaterQualityLog.MAX_PH);

        int tds = readInt("TDS reading (ppm): ");
        double ph = readDouble("pH reading: ");

        System.out.println("\nFilter condition:");
        System.out.println("1. Good");
        System.out.println("2. Needs Replacement");
        String filter = readInt("Choose: ") == 2 ? "Needs Replacement" : "Good";

        WaterQualityLog log = system.addQualityLog(
                LocalDate.now().toString(), tds, ph, filter, checkedBy);

        System.out.println("\nReading recorded. Result: " + log.getResult());

        if (log.getResult().equals("Fail")) {
            System.out.println("WARNING: Water failed the quality check. "
                    + "Stop refilling until resolved.");
        }

        if (filter.equals("Needs Replacement")) {
            System.out.println("REMINDER: Schedule filter/membrane replacement.");
        }
    }

    public void readQuality() {

        System.out.println("\n========== WATER QUALITY LOGS ==========");

        if (system.getQualityLogs().isEmpty()) {
            System.out.println("No readings recorded.");
            return;
        }

        for (WaterQualityLog log : system.getQualityLogs()) {
            System.out.println("--------------------------------");
            log.displayInfo();
        }
    }

    public void myOrders(Customer customer) {

        System.out.println("\n========== MY ORDERS ==========");

        boolean found = false;

        for (Order order : system.getOrders()) {
            if (order.getCustomer() == customer) {
                System.out.println("--------------------------------");
                order.displayInfo();
                found = true;
            }
        }

        if (!found) System.out.println("No orders found.");
    }

    public void cancelMyOrder(Customer customer) {

        myOrders(customer);

        Order order = system.findOrder(readString("\nOrder ID to cancel: "));

        if (order == null || order.getCustomer() != customer) {
            System.out.println("Order not found.");
            return;
        }

        if (!order.getStatus().equals("Pending")) {
            System.out.println("Only Pending orders can be cancelled. "
                    + "Please contact the station.");
            return;
        }

        system.updateOrderStatus(order, "Cancelled");
        System.out.println("\nOrder cancelled.");
    }

    public void mySubscription(Customer customer) {

        System.out.println("\n========== MY SUBSCRIPTION ==========");

        boolean found = false;

        for (Subscription sub : system.getSubscriptions()) {
            if (sub.getCustomer() == customer) {
                sub.displayInfo();
                System.out.println("--------------------------------");
                found = true;
            }
        }

        if (!found) System.out.println("No subscription found.");
    }

    public void myGallons(Customer customer) {

        System.out.println("\n========== MY GALLONS ==========");

        if (customer.getGallons().isEmpty()) {
            System.out.println("You are not holding any station gallons.");
            return;
        }

        for (Gallon g : customer.getGallons()) {
            g.displayInfo();
            System.out.println("--------------------------------");
        }

        System.out.println("Please bring empties back to the station "
                + "for refill or swap.");
    }

    public void myBalance(Customer customer) {

        System.out.println("\n========== MY BALANCE ==========");
        System.out.println("Unpaid balance: "
                + money(system.getCustomerBalance(customer)));
    }

    public void dashboard() {

        System.out.println("\n========== DASHBOARD ==========");

        System.out.println("Today's Sales (paid)    : "
                + money(system.getTodaySales()));
        System.out.println("Gallons Refilled Today  : "
                + system.getTodayRefills());
        System.out.println("Pending Deliveries      : "
                + system.getPendingDeliveries());
        System.out.println("Active Subscriptions    : "
                + system.getActiveSubscriptions());

        System.out.println("\nGallons Ready           : "
                + system.countGallons("Ready"));
        System.out.println("Gallons Needing Refill  : "
                + system.countGallons("Needs Refill"));
        System.out.println("Gallons With Customers  : "
                + system.countGallons("With Customer"));

        WaterQualityLog latest = system.getLatestQualityLog();

        System.out.println();

        if (latest == null) {
            System.out.println("Water Quality           : No readings yet");
        } else {
            System.out.println("Latest Water Quality    : " + latest.getResult()
                    + " (" + latest.getTds() + " ppm, pH "
                    + latest.getPh() + ", " + latest.getDate() + ")");
        }

        if (!system.getLowStockProducts().isEmpty()) {
            System.out.println("\nLOW STOCK:");
            for (Product p : system.getLowStockProducts()) {
                System.out.println(" - " + p.getName()
                        + " (" + p.getStock() + " left)");
            }
        }
    }

    public void reports() {

        System.out.println("\n========== REPORTS ==========");

        System.out.println("Total Sales (paid)        : "
                + money(system.getTotalSales()));
        System.out.println("Unpaid Receivables        : "
                + money(system.getTotalUnpaid()));
        System.out.println("Total Gallons Refilled    : "
                + system.getTotalGallonsRefilled());

        System.out.println("\nCompleted Orders          : "
                + system.getOrderCount("Completed"));
        System.out.println("Pending Orders            : "
                + system.getOrderCount("Pending"));
        System.out.println("Out for Delivery          : "
                + system.getOrderCount("Out for Delivery"));
        System.out.println("Cancelled Orders          : "
                + system.getOrderCount("Cancelled"));

        System.out.println("\nWater Quality Checks Passed: "
                + system.countQualityResult("Pass"));
        System.out.println("Water Quality Checks Failed: "
                + system.countQualityResult("Fail"));

        System.out.println("\nCustomers with Unpaid Balance:");

        boolean any = false;

        for (Customer c : system.getCustomers()) {
            double bal = system.getCustomerBalance(c);
            if (bal > 0) {
                System.out.println(" - " + c.getName() + ": " + money(bal));
                any = true;
            }
        }

        if (!any) System.out.println(" None");
    }

    public Product chooseProduct() {

        System.out.println("\n---------- PRODUCTS ----------");

        for (Product p : system.getProducts()) {
            System.out.println(p.toRow());
        }

        Product p = system.findProduct(readString("\nProduct ID: "));

        if (p == null) {
            System.out.println("Product not found.");
        }

        return p;
    }

    public String gallonType() {

        System.out.println("\n1. Round");
        System.out.println("2. Slim");

        return readInt("Choose gallon type: ") == 2 ? "Slim" : "Round";
    }

    public String orderStatus() {

        System.out.println("\n1. Pending");
        System.out.println("2. Out for Delivery");
        System.out.println("3. Completed");
        System.out.println("4. Cancelled");

        switch (readInt("Choose: ")) {
            case 2: return "Out for Delivery";
            case 3: return "Completed";
            case 4: return "Cancelled";
            default: return "Pending";
        }
    }

    public String subscriptionStatus() {

        System.out.println("1. Active");
        System.out.println("2. Paused");
        System.out.println("3. Cancelled");

        switch (readInt("Choose: ")) {
            case 2: return "Paused";
            case 3: return "Cancelled";
            default: return "Active";
        }
    }

    public String gallonStatus() {

        System.out.println("\n1. Ready");
        System.out.println("2. Needs Refill");
        System.out.println("3. Sanitizing");
        System.out.println("4. Out for Delivery");
        System.out.println("5. With Customer");

        switch (readInt("Choose: ")) {
            case 2: return "Needs Refill";
            case 3: return "Sanitizing";
            case 4: return "Out for Delivery";
            case 5: return "With Customer";
            default: return "Ready";
        }
    }

    public String money(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    public String readString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public int readInt(String message) {

        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public double readDouble(String message) {

        while (true) {
            try {
                System.out.print(message);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public String readDate() {

        while (true) {

            String input = readString(
                    "Date (YYYY-MM-DD, blank = today): ");

            if (input.isEmpty()) {
                return LocalDate.now().toString();
            }

            try {
                return LocalDate.parse(input).toString();
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }
    }

    /**
     * Links a logged-in CUSTOMER account to its in-memory customer record,
     * creating one the first time (until customers are stored through DAOs).
     */
    public Customer findOrCreateCustomer(User user) {
        Customer customer = system.findCustomerByUsername(user.username());
        if (customer == null) {
            customer = system.addCustomer(user.username(), user.password(),
                    user.fullName(), "Not set", "Not set");
        }
        return customer;
    }
}
