package com.dblappdev.app.dataClasses;

public class User {
    private String username;
    private String email;

    public User (String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User (String username) {
        this(username, "");
    }

    /**
     * gets the email of the user
     * @return {@code this.email}
     */
    public String getEmail() {
        return email;
    }

    /**
     * gets the username of the user
     * @return {@code this.username}
     */
    public String getUsername() {
        return username;
    }
}
