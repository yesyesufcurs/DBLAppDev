package com.dblappdev.app.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

public class LoginService {
    /**
     * Registers user in backend and returns apiKey
     *
     * @param username desired username of the new user (max length: 30)
     * @param password desired password of the new user (min length: 6)
     * @param email    desired email of the new user    (must be valid)
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code username == null || password == null
     *                                  || email == null || context == null || response == null}
     * @throws IllegalArgumentException if {@code username.length() >= 30 || password.length() < 6
     *                                  || password.length() >= 30 || !isASCII(username)
     *                                  || !isASCII(password) || email is invalid}
     * @pre {@code username != null && password != null && email != null && username.length() < 30
     * && isASCII(username) && isAscii(password) && 6 <= password.length() < 30 && email is valid
     * && context != null && response != null}
     * @post {@code APIResponse.data == apiKey}
     */
    public static void register(String username, String password, String email, Context context,
                                APIResponse<String> response) {
        // Check preconditions
        if (username == null || password == null || email == null) {
            throw new IllegalArgumentException("LoginService.register.pre: username, password or " +
                    "email is null.");
        }
        if (username.length() >= 30) {
            String errorMessage = "Username must be shorter than 30 characters.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        if (!isASCII(username)) {
            String errorMessage = "Username must only contain ASCII characters";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        if (password.length() < 6 || password.length() >= 30) {
            String errorMessage = "Password must be at least 6 and less than 30 characters.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        if (!isASCII(password)) {
            String errorMessage = "Password may only contain ASCII characters";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        // Source: https://emailregex.com/
        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}" +
                "~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\" +
                "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
                "\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]" +
                "[0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:" +
                "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b" +
                "\\x0c\\x0e-\\x7f])+)\\])")) {
            String errorMessage = "Enter a valid email address.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        // Set headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("username", username);
        headers.put("password", password);
        headers.put("email", email);

        // Modify response:
        APIResponse<String> modifiedResponse = new APIResponse<String>() {
            @Override
            public void onResponse(String data) {
                response.onResponse(data);
            }

            @Override
            public void onErrorResponse(VolleyError error, String errorMessage) {
                if ("username already exists".equals(errorMessage)) {
                    login(username, password, context, response);
                } else {
                    response.onErrorResponse(error, errorMessage);
                }
            }
        };

        // Invoke Request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "register", Request.Method.GET,
                headers, null).run(context, modifiedResponse, new APIResponse<String>() {

            @Override
            public void onResponse(String data) {
                login(username, password, context, response);
            }

            @Override
            public void onErrorResponse(VolleyError error, String errorMessage) {
                response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            }
        });
    }

    /**
     * Logs user in and returns apiKey.
     *
     * @param username username of the user logging in
     * @param password password of the user logging in
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code username == null || password == null ||
     *                                  context == null || response == null}}
     * @pre {@code username != null && password != null && context != null && response != null}
     * @post {@code APIResponse.data == apiKey}
     */
    public static void login(String username, String password, Context context,
                             APIResponse<String> response) {
        //Check preconditions
        if (username == null || password == null) {
            throw new IllegalArgumentException("LoginService.login.pre: username or password" +
                    "is null");
        }

        if (username.length() >= 30 || password.length() >= 30) {
            String errorMessage = "Username and password must be shorter than 30 characters.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "login", Request.Method.GET,
                params, null).run(context, response);

    }

    private static boolean isASCII(String string) {
        for (char c : string.toCharArray()) {
            // The characters between 0 - 127 are the ASCII characters
            if (c >= 128) {
                return false;
            }
        }
        return true;
    }
}
