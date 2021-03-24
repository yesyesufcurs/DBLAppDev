package com.dblappdev.app.dataClasses;

import android.content.Context;

import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;

public class LoggedInUser {

    private User user;
    private String apiKey;
    private static LoggedInUser instance;

    //Private to force use of logIn
    private LoggedInUser (String apiKey, String username, String email) {
        user = new User(username, email);
        this.apiKey = apiKey;
    }

    /**
     * creates an instance of LoggedInUser if none exist yet. Saves it to this.instance
     * @throws IllegalStateException if {@code instance != null}
     * @pre {@code instance == null}
     * @post {@code this.instance.getApiKey() == apiKey
     * && this.instance.getUser().getUsername() == username
     * && this.instance.getUser().getEmail() == email}
     */
    public static void logIn(String apiKey, String username, String email) {
        if (instance == null) {
            instance = new LoggedInUser(apiKey, username, email);
        } else {
            throw new IllegalStateException("Cannot log in: already logged in");
        }
    }

    public static void logIn(String apiKey, String username) {
        logIn(apiKey, username, "");
    }

    public static void logOut() {
        instance = null;
    }

    /**
     * gets the loggedInUser instance
     * @return {@code this.instance}
     */
    public static LoggedInUser getInstance() {
        return instance;
    }

    /**
     * gets the apiKey
     * @return {@code this.apiKey}
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * gets the user
     * @return {@code this.user}
     */
    public User getUser() {
        return user;
    }
}
