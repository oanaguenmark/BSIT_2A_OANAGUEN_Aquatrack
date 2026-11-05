package com.aquatrack.service;

import com.aquatrack.model.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class RefillStationSystem {

    public static final String STATION_NAME = "AquaTrack";
    public static final double DELIVERY_FEE = 20.0;   // flat fee per delivery order
    public static final int LOW_STOCK_LIMIT = 5;

    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Gallon> gallons = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    private ArrayList<Subscription> subscriptions = new ArrayList<>();
    private ArrayList<WaterQualityLog> qualityLogs = new ArrayList<>();

    private int customerNumber = 1;
    private int productNumber = 1;
    private int gallonNumber = 1;
    private int orderNumber = 1;
    private int subscriptionNumber = 1;
    private int logNumber = 1;

    public RefillStationSystem() {
        createSampleData();
    }

    public static String today() {
        return LocalDate.now().toString();
    }

    // =====================================================
    // CUSTOMERS
    // =====================================================

    public Customer addCustomer(String username, String password,
                                String name, String address,
                                String contact) {

        String id = String.format("C%03d", customerNumber++);
        Customer customer = new Customer(
                id, username, password, name, address, contact);
        customers.add(customer);
        return customer;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public Customer findCustomer(String id) {
        for (Customer c : customers) {
            if (c.getCustomerId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    public Customer findCustomerByUsername(String username) {
        for (Customer c : customers) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                return c;
            }
        }
        return null;
    }

    public boolean deleteCustomer(String id) {

        Customer customer = findCustomer(id);
        if (customer == null) {
            return false;
        }

        customers.remove(customer);
        subscriptions.removeIf(s -> s.getCustomer() == customer);

        // cancel open delivery orders (restores stock); keep completed
        // orders so sales history and unpaid balances stay accurate
        for (Order o : orders) {
            if (o.getCustomer() == customer) {
                updateOrderStatus(o, "Cancelled");
            }
        }

        // gallons go back to the station
        for (Gallon g : new ArrayList<>(customer.getGallons())) {
            customer.removeGallon(g);
            g.setStatus("Needs Refill");
            g.setCurrentLocation("Station");
        }

        return true;
    }

    public double getCustomerBalance(Customer customer) {
        double total = 0;
        for (Order o : orders) {
            if (o.getCustomer() == customer
                    && !o.getStatus().equals("Cancelled")
                    && o.getPaymentStatus().equals("Unpaid")) {
                total += o.getTotal();
            }
        }
        return total;
    }

    // =====================================================
    // PRODUCTS & STOCK
    // =====================================================

    public Product addProduct(String name, double price,
                              int stock, boolean refill) {

        String id = String.format("P%03d", productNumber++);
        Product product = new Product(id, name, price, stock, refill);
        products.add(product);
        return product;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public Product findProduct(String id) {
        for (Product p : products) {
            if (p.getProductId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    public boolean deleteProduct(String id) {
        Product product = findProduct(id);
        if (product != null) {
            products.remove(product);
            return true;
        }
        return false;
    }

    public ArrayList<Product> getLowStockProducts() {
        ArrayList<Product> low = new ArrayList<>();
        for (Product p : products) {
            if (!p.isRefill() && p.getStock() <= LOW_STOCK_LIMIT) {
                low.add(p);
            }
        }
        return low;
    }

    // =====================================================
    // GALLONS
    // =====================================================

    public Gallon addGallon(String type, String location, String status) {

        String id = String.format("G-%03d", gallonNumber++);
        Gallon gallon = new Gallon(id, type, location, status);
        gallons.add(gallon);
        return gallon;
    }

    public ArrayList<Gallon> getGallons() {
        return gallons;
    }

    public Gallon findGallon(String id) {
        for (Gallon g : gallons) {
            if (g.getGallonId().equalsIgnoreCase(id)) {
                return g;
            }
        }
        return null;
    }

    public Customer findGallonHolder(Gallon gallon) {
        for (Customer c : customers) {
            if (c.getGallons().contains(gallon)) {
                return c;
            }
        }
        return null;
    }

    /** Hand a gallon to a customer. */
    public void assignGallon(Gallon gallon, Customer customer) {

        Customer previous = findGallonHolder(gallon);
        if (previous != null) {
            previous.removeGallon(gallon);
        }

        customer.addGallon(gallon);
        gallon.setStatus("With Customer");
        gallon.setCurrentLocation(customer.getName() + "'s place");
    }

    /** Customer brings an empty gallon back to the station. */
    public void returnGallon(Gallon gallon) {

        Customer holder = findGallonHolder(gallon);
        if (holder != null) {
            holder.removeGallon(gallon);
        }

        gallon.setStatus("Needs Refill");
        gallon.setCurrentLocation("Station");
    }

    public boolean deleteGallon(String id) {

        Gallon gallon = findGallon(id);
        if (gallon == null) {
            return false;
        }

        Customer holder = findGallonHolder(gallon);
        if (holder != null) {
            holder.removeGallon(gallon);
        }

        gallons.remove(gallon);
        return true;
    }

    public int countGallons(String status) {
        int count = 0;
        for (Gallon g : gallons) {
            if (g.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    // =====================================================
    // ORDERS
    // =====================================================

    /**
     * Places an order. Returns null if the quantity is invalid,
     * stock is not enough, or a delivery has no customer.
     * orderType: "Walk-in" or "Delivery".
     */
    public Order placeOrder(Customer customer, Product product,
                            int quantity, String orderType,
                            String date, boolean paidNow) {

        if (product == null || quantity <= 0
                || !product.hasStock(quantity)) {
            return null;
        }

        boolean delivery = orderType.equals("Delivery");
        if (delivery && customer == null) {
            return null;
        }

        double total = product.getPrice() * quantity;
        String address = "Station (Walk-in)";

        if (delivery) {
            total += DELIVERY_FEE;
            address = customer.getAddress();
        }

        product.reduceStock(quantity);

        String id = String.format("O%03d", orderNumber++);
        Order order = new Order(
                id, customer, product, quantity, orderType,
                date, address, total,
                paidNow ? "Paid" : "Unpaid",
                delivery ? "Pending" : "Completed");

        orders.add(order);
        return order;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public Order findOrder(String id) {
        for (Order o : orders) {
            if (o.getOrderId().equalsIgnoreCase(id)) {
                return o;
            }
        }
        return null;
    }

    /** Completed and Cancelled orders are final. */
    public boolean updateOrderStatus(Order order, String newStatus) {

        String current = order.getStatus();
        if (current.equals("Completed") || current.equals("Cancelled")) {
            return false;
        }

        if (newStatus.equals("Cancelled")) {
            order.getProduct().addStock(order.getQuantity());
        }

        order.setStatus(newStatus);
        return true;
    }

    public void markOrderPaid(Order order) {
        order.setPaymentStatus("Paid");
    }

    public boolean deleteOrder(String id) {

        Order order = findOrder(id);
        if (order == null) {
            return false;
        }

        String s = order.getStatus();
        if (!s.equals("Completed") && !s.equals("Cancelled")) {
            order.getProduct().addStock(order.getQuantity());
        }

        orders.remove(order);
        return true;
    }

    // =====================================================
    // SUBSCRIPTIONS
    // =====================================================

    public Subscription addSubscription(Customer customer,
                                        String gallonType,
                                        String schedule,
                                        int quantity) {

        String id = String.format("S%03d", subscriptionNumber++);
        Subscription sub = new Subscription(
                id, customer, gallonType, schedule, quantity, "Active");
        subscriptions.add(sub);
        return sub;
    }

    public ArrayList<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public Subscription findSubscription(String id) {
        for (Subscription s : subscriptions) {
            if (s.getSubscriptionId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    public boolean deleteSubscription(String id) {
        Subscription sub = findSubscription(id);
        if (sub != null) {
            subscriptions.remove(sub);
            return true;
        }
        return false;
    }

    /** Creates this period's delivery order for an Active subscription. */
    public Order generateOrderFromSubscription(Subscription sub, String date) {

        if (!sub.getStatus().equals("Active")) {
            return null;
        }

        Product refill = null;
        for (Product p : products) {
            if (p.isRefill() && p.getName().toLowerCase()
                    .contains(sub.getGallonType().toLowerCase())) {
                refill = p;
                break;
            }
        }

        return placeOrder(sub.getCustomer(), refill,
                sub.getQuantity(), "Delivery", date, false);
    }

    // =====================================================
    // WATER QUALITY
    // =====================================================

    public WaterQualityLog addQualityLog(String date, int tds, double ph,
                                         String filterStatus,
                                         String checkedBy) {

        String id = String.format("Q%03d", logNumber++);
        WaterQualityLog log = new WaterQualityLog(
                id, date, tds, ph, filterStatus, checkedBy);
        qualityLogs.add(log);
        return log;
    }

    public ArrayList<WaterQualityLog> getQualityLogs() {
        return qualityLogs;
    }

    public WaterQualityLog getLatestQualityLog() {
        if (qualityLogs.isEmpty()) {
            return null;
        }
        return qualityLogs.get(qualityLogs.size() - 1);
    }

    public int countQualityResult(String result) {
        int count = 0;
        for (WaterQualityLog log : qualityLogs) {
            if (log.getResult().equals(result)) {
                count++;
            }
        }
        return count;
    }

    // =====================================================
    // DASHBOARD & REPORTS
    // =====================================================

    public double getTodaySales() {
        double total = 0;
        for (Order o : orders) {
            if (o.getOrderDate().equals(today())
                    && o.getPaymentStatus().equals("Paid")
                    && !o.getStatus().equals("Cancelled")) {
                total += o.getTotal();
            }
        }
        return total;
    }

    public int getTodayRefills() {
        int count = 0;
        for (Order o : orders) {
            if (o.getOrderDate().equals(today())
                    && o.getProduct().isRefill()
                    && !o.getStatus().equals("Cancelled")) {
                count += o.getQuantity();
            }
        }
        return count;
    }

    public int getPendingDeliveries() {
        int count = 0;
        for (Order o : orders) {
            if (o.getOrderType().equals("Delivery")
                    && (o.getStatus().equals("Pending")
                    || o.getStatus().equals("Out for Delivery"))) {
                count++;
            }
        }
        return count;
    }

    public int getActiveSubscriptions() {
        int count = 0;
        for (Subscription s : subscriptions) {
            if (s.getStatus().equals("Active")) {
                count++;
            }
        }
        return count;
    }

    public double getTotalSales() {
        double total = 0;
        for (Order o : orders) {
            if (o.getPaymentStatus().equals("Paid")
                    && !o.getStatus().equals("Cancelled")) {
                total += o.getTotal();
            }
        }
        return total;
    }

    public double getTotalUnpaid() {
        double total = 0;
        for (Order o : orders) {
            if (o.getPaymentStatus().equals("Unpaid")
                    && !o.getStatus().equals("Cancelled")) {
                total += o.getTotal();
            }
        }
        return total;
    }

    public int getOrderCount(String status) {
        int count = 0;
        for (Order o : orders) {
            if (o.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    public int getTotalGallonsRefilled() {
        int count = 0;
        for (Order o : orders) {
            if (o.getProduct().isRefill()
                    && o.getStatus().equals("Completed")) {
                count += o.getQuantity();
            }
        }
        return count;
    }

    // =====================================================
    // SAMPLE DATA
    // =====================================================

    private void createSampleData() {

        // products
        Product roundRefill = addProduct("5-Gal Round Refill", 25.00, 0, true);
        addProduct("5-Gal Slim Refill", 25.00, 0, true);
        Product newRound = addProduct("New Round Gallon (w/ cap)", 180.00, 12, false);
        addProduct("New Slim Gallon (w/ cap)", 200.00, 8, false);
        addProduct("Replacement Gallon Cap", 15.00, 4, false);
        addProduct("Bottled Water 500ml (case of 24)", 240.00, 20, false);

        // customers
        Customer carlo = addCustomer("carlo", "1234", "Carlo Tañedo",
                "Talisay City, Cebu", "0917-000-0001");
        Customer juan = addCustomer("juan", "1234", "Juan Dela Cruz",
                "Minglanilla, Cebu", "0917-000-0002");

        // gallons
        Gallon g1 = addGallon("Round", "Station", "Ready");
        addGallon("Round", "Station", "Ready");
        addGallon("Slim", "Station", "Needs Refill");
        addGallon("Round", "Delivery Vehicle", "Out for Delivery");
        assignGallon(g1, carlo);

        // subscriptions
        addSubscription(carlo, "Round", "Every Monday & Thursday", 3);
        addSubscription(juan, "Slim", "Every Saturday", 2);

        // orders
        placeOrder(null, roundRefill, 3, "Walk-in", today(), true);
        placeOrder(null, newRound, 1, "Walk-in", today(), true);
        placeOrder(carlo, roundRefill, 3, "Delivery", today(), false);
        Order done = placeOrder(juan, roundRefill, 2, "Delivery", today(), true);
        updateOrderStatus(done, "Completed");

        // water quality
        addQualityLog(today(), 18, 7.2, "Good", "System Administrator");
    }
}
