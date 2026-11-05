package com.aquatrack.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Opens SQLite connections with foreign key enforcement turned on. */
public final class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:aquatrack.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // SQLite ignores foreign keys unless this is set on EVERY connection.
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }
}
