package com.aquatrack.model;

public class Person {

    protected String username;
    protected String password;
    protected String name;

    public Person(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username)
                && this.password.equals(password);
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void displayRole() {
        System.out.println("Role: User");
    }
}
