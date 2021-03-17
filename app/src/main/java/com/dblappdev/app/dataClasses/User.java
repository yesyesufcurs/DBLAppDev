package com.dblappdev.app.dataClasses;

public class User {
    private String username;
    private String email;

    public User (String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
