package com.aquatrack.model;

/** Immutable data model for a row in the activity_logs table. */
public record ActivityLog(int logId, int userId, String actionType,
                          String description, String timestamp) {
}
