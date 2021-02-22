package com.dblappdev.app;

import org.json.JSONArray;

public class APIService {
    /**
     * Registers user in backend and returns apiKey
     *
     * @param username
     * @param password
     * @param email
     * @throws IllegalArgumentException if {@code username == null || password == null
     *                                  || email == null}
     * @throws IllegalArgumentException if {@code username.length() > 30 || password.length() < 6
     *                                  || email is valid}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code username != null && password != null && email != null && username.length() <= 30
     * && password.length() >= 6 && email is valid}
     * @returns apiKey
     * @post {@code \result == apiKey}
     */
    public String register(String username, String password, String email) {
        if (username == null || password == null || email == null) {
            throw new IllegalArgumentException("APIService.register.pre: username, password or " +
                    "email is null.");
        }
        if (username.length() >= 30) {
            throw new IllegalArgumentException("APIService.register.pre: username should be " +
                    "shorter than 30 characters.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("APIService.register.pre: password should be at " +
                    "least 6 characters.");
        }
        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}" +
                "~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\" +
                "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
                "\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]" +
                "[0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:" +
                "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b" +
                "\\x0c\\x0e-\\x7f])+)\\])")) {
            throw new IllegalArgumentException("APIService.register.pre: email is not valid.");
        }
        return null;
    }

    /**
     * Logs user in and returns apiKey.
     *
     * @param username
     * @param password
     * @return apiKey
     * @throws IllegalArgumentException if {@code username == null || password == null}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code username != null && password != null}
     * @post {@code \result == apiKey}
     */
    public String login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("APIService.login.pre: username or password" +
                    "is null");
        }

        return null;
    }

    /**
     * Returns JSONArray containing expense groups a user is part of.
     * Each entry is of the form expense_group_id, expense_group_name, user_id of moderator.
     *
     * @param apiKey
     * @return expenseGroups
     * @throws IllegalArgumentException if {@code apiKey == null}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code apiKey != null}
     * @post {@code \result == expenseGroups}
     */
    public JSONArray getExpenseGroups(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroups.pre: apiKey is null");
        }
        return null;
    }

    /**
     * Returns JSONArray containing all available expense groups a user is part of
     * Each entry is of the form expense_group_id, expense_group_name, user_id of moderator.
     *
     * @param apiKey
     * @return allExpenseGroups
     * @throws IllegalArgumentException if {@code apiKey == null}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code apiKey != null}
     * @post {@code \result == expenseGroups}
     */
    public JSONArray getAllExpenseGroups(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getAllExpenseGroups.pre: apiKey is" +
                    " null");
        }
        return null;
    }

    /**
     * Creates new expenseGroup and returns the assigned expense_group_id.
     *
     * @param apiKey
     * @param expenseGroupName
     * @return expense_group_id
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupName == null}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code apiKey != null && expenseGroupName != null}
     * @post {@code \result == expenseGroups}
     */
    public String createExpenseGroup(String apiKey, String expenseGroupName) {
        if (apiKey == null || expenseGroupName == null) {
            throw new IllegalArgumentException("APIService.createExpenseGroup.pre: apiKey or " +
                    "expenseGroupName is null");
        }

        return null;
    }

    /**
     * Returns userIds of expense group members
     *
     * @param apiKey
     * @param expenseGroupId
     * @return JSONArray containing all userIds of an expense group
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code apiKey != null && expenseGroupId != null}
     * @post {@code \result == userIds}
     */
    public JSONArray getExpenseGroupMembers(String apiKey, String expenseGroupId) {
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroupMembers.pre: apiKey or" +
                    "expenseGroupId is null");
        }

        return null;
    }

    /**
     * Adds user to expense group.
     *
     * @param apiKey
     * @param userId
     * @param expenseGroupId
     * @throws IllegalArgumentException if {@code apiKey == null || userId == null
     *                                  expenseGroupId}
     * @pre {@code apiKey != null && userId != null && expenseGroupId != null}
     * @post {@code userId is member of expenseGroupId}
     */
    public void addToExpenseGroup(String apiKey, String userId, String expenseGroupId) {
        if (apiKey == null || userId == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.addToExpenseGroup.pre:" +
                    "apiKey or userId or expenseGroupId is null");
        }
    }

    //TODO Add expense.py methods

}
