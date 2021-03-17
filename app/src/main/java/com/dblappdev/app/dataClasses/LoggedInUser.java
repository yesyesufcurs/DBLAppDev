package com.dblappdev.app.dataClasses;

import android.content.Context;

import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;

public class LoggedInUser {

    private User user;
    private String apiKey;
    private static LoggedInUser instance;

    private LoggedInUser (String apiKey, String username, String email) {
        user = new User(username, email);
        this.apiKey = apiKey;
    }

    public static void logIn(String apiKey, String username, String email) {
        if (instance == null) {
            instance = new LoggedInUser(apiKey, username, email);
        }
    }

    public LoggedInUser getInstance() {
        return instance;
    }

    public String getApiKey() {
        return apiKey;
    }

    public User getUser() {
        return user;
    }
}
