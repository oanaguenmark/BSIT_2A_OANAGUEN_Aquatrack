package com.aquatrack.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/** Creates all AquaTrack tables (from the approved ERD) if they do not exist yet. */
public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    // Order matters: parent tables first so foreign keys can reference them.
    private static final List<String> TABLES = List.of(
        """
        CREATE TABLE IF NOT EXISTS users (
            user_id   INTEGER PRIMARY KEY AUTOINCREMENT,
            username  TEXT NOT NULL UNIQUE,
            password  TEXT NOT NULL,
            full_name TEXT NOT NULL,
            role      TEXT NOT NULL CHECK (role IN ('ADMIN','STAFF','CUSTOMER')),
            status    TEXT NOT NULL DEFAULT 'PENDING'
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS customers (
            customer_id    INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id        INTEGER NOT NULL UNIQUE,
            address        TEXT,
            contact_number TEXT,
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS products (
            product_id INTEGER PRIMARY KEY AUTOINCREMENT,
            name       TEXT NOT NULL,
            price      REAL NOT NULL,
            stock      INTEGER NOT NULL DEFAULT 0,
            is_refill  INTEGER NOT NULL DEFAULT 0 CHECK (is_refill IN (0,1))
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS orders (
            order_id       INTEGER PRIMARY KEY AUTOINCREMENT,
            customer_id    INTEGER NOT NULL,
            product_id     INTEGER NOT NULL,
            quantity       INTEGER NOT NULL,
            order_type     TEXT,
            order_date     TEXT,
            address        TEXT,
            total          REAL,
            payment_status TEXT,
            status         TEXT,
            FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
            FOREIGN KEY (product_id)  REFERENCES products(product_id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS gallons (
            gallon_id        INTEGER PRIMARY KEY AUTOINCREMENT,
            type             TEXT,
            current_location TEXT,
            status           TEXT,
            customer_id      INTEGER,
            FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS subscriptions (
            subscription_id INTEGER PRIMARY KEY AUTOINCREMENT,
            customer_id     INTEGER NOT NULL,
            gallon_type     TEXT,
            schedule        TEXT,
            quantity        INTEGER,
            status          TEXT,
            FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS water_quality_logs (
            log_id        INTEGER PRIMARY KEY AUTOINCREMENT,
            log_date      TEXT,
            tds           INTEGER,
            ph            REAL,
            filter_status TEXT,
            checked_by    INTEGER,
            result        TEXT,
            FOREIGN KEY (checked_by) REFERENCES users(user_id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS activity_logs (
            activity_id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id     INTEGER NOT NULL,
            action      TEXT NOT NULL,
            details     TEXT,
            timestamp   TIMESTAMP DEFAULT (datetime('now', 'localtime')),
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
        )
        """
    );

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            upgradeEmptyActivityLogs(st);
            for (String sql : TABLES) {
                st.execute(sql);
            }
            System.out.println("[DB] Database initialized (aquatrack.db).");
        } catch (SQLException e) {
            throw new IllegalStateException("Could not initialize database: " + e.getMessage(), e);
        }
    }

    /**
     * Earlier versions created activity_logs without ON DELETE CASCADE. If that old,
     * still-empty table is found, drop it so it is rebuilt with the cascade rule.
     */
    private static void upgradeEmptyActivityLogs(Statement st) throws SQLException {
        boolean exists = false;
        boolean cascade = false;
        try (ResultSet rs = st.executeQuery("PRAGMA foreign_key_list(activity_logs)")) {
            while (rs.next()) {
                exists = true;
                if ("CASCADE".equalsIgnoreCase(rs.getString(7))) { // column 7 = on_delete
                    cascade = true;
                }
            }
        }
        if (exists && !cascade) {
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM activity_logs")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.execute("DROP TABLE activity_logs");
                }
            }
        }
    }
}
