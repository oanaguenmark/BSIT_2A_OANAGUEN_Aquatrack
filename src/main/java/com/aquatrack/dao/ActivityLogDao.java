package com.aquatrack.dao;

import com.aquatrack.model.ActivityLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** All SQL for the activity_logs table (audit trail) lives here. */
public class ActivityLogDao {

    private static final String COLUMNS =
        "activity_id, user_id, action, details, timestamp";

    /** Records one action. A logging problem never stops the app. */
    public void logAction(int userId, String actionType, String description) {
        String sql = "INSERT INTO activity_logs (user_id, action, details) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, actionType);
            ps.setString(3, description);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not write activity log: " + e.getMessage());
        }
    }

    /** All logs, newest first. */
    public List<ActivityLog> getAllLogs() {
        String sql = "SELECT " + COLUMNS
                   + " FROM activity_logs ORDER BY timestamp DESC, activity_id DESC";
        List<ActivityLog> logs = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return logs;
    }

    /** Logs for one user, newest first. */
    public List<ActivityLog> getLogsByUserId(int userId) {
        String sql = "SELECT " + COLUMNS
                   + " FROM activity_logs WHERE user_id = ? ORDER BY timestamp DESC, activity_id DESC";
        List<ActivityLog> logs = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return logs;
    }

    /** Deletes every log entry. Returns how many rows were removed. */
    public int clearAllLogs() {
        String sql = "DELETE FROM activity_logs";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not clear logs: " + e.getMessage());
            return 0;
        }
    }

    private ActivityLog mapRow(ResultSet rs) throws SQLException {
        return new ActivityLog(
            rs.getInt("activity_id"),
            rs.getInt("user_id"),
            rs.getString("action"),
            rs.getString("details"),
            rs.getString("timestamp"));
    }
}
