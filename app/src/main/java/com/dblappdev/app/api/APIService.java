package com.dblappdev.app;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public static void register(String username, String password, String email) {
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
        // Source: https://emailregex.com/
        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}" +
                "~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\" +
                "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
                "\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]" +
                "[0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:" +
                "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b" +
                "\\x0c\\x0e-\\x7f])+)\\])")) {
            throw new IllegalArgumentException("APIService.register.pre: email is not valid.");
        }
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
    public static void login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("APIService.login.pre: username or password" +
                    "is null");
        }

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
    public static void getExpenseGroups(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroups.pre: apiKey is null");
        }
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
    public static void getAllExpenseGroups(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getAllExpenseGroups.pre: apiKey is" +
                    " null");
        }
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
    public static void createExpenseGroup(String apiKey, String expenseGroupName) {
        if (apiKey == null || expenseGroupName == null) {
            throw new IllegalArgumentException("APIService.createExpenseGroup.pre: apiKey or " +
                    "expenseGroupName is null");
        }

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
    public void getExpenseGroupMembers(String apiKey, String expenseGroupId) {
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroupMembers.pre: apiKey or" +
                    "expenseGroupId is null");
        }

    }

    /**
     * Adds user to expense group.
     *
     * @param apiKey
     * @param userId
     * @param expenseGroupId
     * @throws IllegalArgumentException if {@code apiKey == null || userId == null
     *                                  expenseGroupId}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code apiKey != null && userId != null && expenseGroupId != null}
     * @post {@code \result in getExpenseGroupMembers(apiKey, expenseGroupId)}
     */
    public static void addToExpenseGroup(String apiKey, String userId, String expenseGroupId) {
        if (apiKey == null || userId == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.addToExpenseGroup.pre:" +
                    "apiKey or userId or expenseGroupId is null");
        }
    }

    /**
     * Creates expense and returns expense id of created expense.
     *
     * @param apiKey
     * @param title
     * @param amount
     * @param picture
     * @param description
     * @param expenseGroupId
     * @return expenseId
     * @throws IllegalArgumentException if {@code apiKey == null || title == null ||
     *                                  amount == null || picture == null ||
     *                                  description == null || expenseGroupId == null}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code apiKey != null && title != null && amount != null &&
     * picture != null && description != null && expenseGroupId != null}
     * @post {@code \result in getExpenseGroupExpenses(apiKey, expenseGroupId)}
     */
    public static void createExpense(String apiKey, String title, String amount, String picture,
                                String description, String expenseGroupId) {
        if (apiKey == null || title == null || amount == null || picture == null ||
                description == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.createExpense.pre: apiKey or " +
                    "title or amount or picture or description or expenseGroupId is null");
        }

    }

    /**
     * Creates expenseIOU i.e. the amount of money each person in a expense group
     * owes the creator of an expense.
     *
     * @param apiKey
     * @param expenseId
     * @param iouJson
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null ||
     *                                  iouJson == null}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code apiKey != null && expenseId != null && iouJson != null}
     * @post {@code iouJson == getOwedExpenses(apiKey, expenseId)}
     */
    public static void createExpenseIOU(String apiKey, String expenseId, JSONObject iouJson) {
        if (apiKey == null || expenseId == null || iouJson == null) {
            throw new IllegalArgumentException("APIService.createExpenseIOU.pre: apiKey or " +
                    "expenseId or iouJson is null");
        }

    }

    /**
     * Gets expense details of all expenses in an expense group.
     *
     * @param apiKey
     * @param expenseGroupId
     * @return expenseGroupExpenses
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null}
     * @throws IllegalStateException    if backend returns an error
     * @pre {@code apiKey != null && expenseGroupId != null}
     * @post {@code \result == expenseGroupExpenses : expenseGroupExpenses.expenseGroupId ==
     * expenseGroupId}
     */
    public static void getExpenseGroupExpenses(String apiKey, String expenseGroupId) {
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroupExpenses.pre: " +
                    "apiKey or expenseGroupId is null");
        }

    }

    /**
     * Returns expense details of all expenses created by the user that makes the request
     *
     * @param apiKey
     * @throws IllegalArgumentException if {@code apiKey == null}
     * @throws IllegalStateException    if backend returns an error
     * @returns userExpenses
     * @pre {@code apiKey != null}
     * @post {@code \result == userExpenses : userExpenses.expenseGroupId == apiKey.user}
     */
    public static void getUsersExpenses(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getUserExpenses.pre: apiKey is null");
        }


    }

    /**
     * Returns all expenses where the user owes someone else money
     *
     * @param apiKey
     * @throws IllegalArgumentException if {@code apiKey == null}
     * @throws IllegalStateException    if backend returns an error
     * @return userOwedExpenses
     * @pre {@code apiKey != null}
     * @post {@code \result == userOwedExpenses : \forall i; userOwedExpenses.ids().has(i);
     * getOwedExpenses(userOwedExpenses[i]).ower() == apiKey.user}
     */
    public static void getUserOwedExpenses(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getUserExpenses.pre: apiKey is null");
        }
    }


    /**
     * Returns the details of an expense given an expenseId
     *
     * @param apiKey
     * @param expenseId
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null}
     * @throws IllegalStateException    if backend returns an error
     * @return expenseDetails
     * @pre {@code apiKey != null && expenseId != null}
     * @post {@code \result == expenseDetails : expenseDetails.id() == expenseId}
     */
    public static void getExpenseDetails(String apiKey, String expenseId) {
        if (apiKey == null || expenseId == null) {
            throw new IllegalArgumentException("APIService.getExpenseDetails.pre: " +
                    "apiKey or expenseId is null.");
        }


    }

    /**
     * Returns how much each person owes the expense creator given expenseId
     *
     * @param apiKey
     * @param expenseId
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null}
     * @throws IllegalStateException    if backend returns an error
     * @return owedExpenses
     * @pre {@code apiKey != null && expenseId != null}
     * @post {@code \result == owedExpenses: owedExpenses.expenseId() == expenseId}
     */
    public static void getOwedExpenses(String apiKey, String expenseId) {
        if (apiKey == null || expenseId == null) {
            throw new IllegalArgumentException("APIService.getOwedExpenses.pre: " +
                    "apiKey or expenseId is null.");
        }

    }


}
