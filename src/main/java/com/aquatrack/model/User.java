package com.aquatrack.model;

/** Immutable data model for a row in the users table. */
public record User(int userId, String username, String password,
                   String fullName, Role role, String status) {
}
