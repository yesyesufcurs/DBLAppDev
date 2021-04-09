package com.dblappdev.app.api;

import android.content.Context;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseGroupService {

    /**
     * Returns List<Map<String, String>>
     * Each entry contains name, moderator_id.
     *
     * @param apiKey         apiKey of the user calling this method
     * @param expenseGroupId id of the expense group
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || context == null ||
     *                                  || expenseGroupId == null || response == null}}
     * @pre {@code apiKey != null && expenseGroupId != null && context != null
     * && response != null}
     * @post {@code APIResponse.data == expenseGroups}
     */
    public static void getExpenseGroup(String apiKey, String expenseGroupId, Context context,
                                       APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroup.pre: apiKey or" +
                    "expenseGroupId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_group_id", expenseGroupId);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getExpenseGroup",
                Request.Method.GET, params, null).run(context, response);
    }

    /**
     * Returns List<Map<String, String>> object containing expense groups a user is part of.
     * Each entry contains id, name, moderator_id.
     *
     * @param apiKey   apiKey of the user calling this method
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || context == null ||
     *                                  response == null}}
     * @pre {@code apiKey != null && context != null && response != null}
     * @post {@code APIResponse.data == expenseGroups}
     */
    public static void getExpenseGroups(String apiKey, Context context,
                                        APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroups.pre: apiKey is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getExpenseGroups",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Creates new expenseGroup and returns the assigned expense_group_id.
     *
     * @param apiKey           apiKey of the user calling this method
     * @param expenseGroupName name of the expense group
     * @param context          context of request, often AppActivity (instance of calling object)
     * @param response         contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupName == null ||
     *                                  context == null || response == null}}
     * @pre {@code apiKey != null && expenseGroupName != null && context != null && response != null}
     * @post {@code APIResponse.data == expenseGroups}
     */
    public static void createExpenseGroup(String apiKey, String expenseGroupName, Context
            context,
                                          APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || expenseGroupName == null) {
            throw new IllegalArgumentException("APIService.createExpenseGroup.pre: apiKey or " +
                    "expenseGroupName is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("expense_group_name", expenseGroupName);
        params.put("api_key", apiKey);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "createExpenseGroup",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Removes an expenseGroup where all debt has been paid back
     *
     * @param apiKey         apiKey of the user calling this method
     * @param expenseGroupId id of the expense group
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null ||
     *                                  context == null || response == null}
     * @pre {@code apiKey != null && expenseGroupId != null && context != null & response != null}
     * @post {@code expenseGroupId not in getAllExpenseGroups()}
     */
    public static void removeExpenseGroup(String apiKey, String expenseGroupId, Context context,
                                          APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.removeExpenseGroup.pre: apiKey or " +
                    "expenseGroupId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_group_id", expenseGroupId);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "removeExpenseGroup",
                Request.Method.GET, params, null).run(context, response);
    }

    /**
     * Returns List<Map<String, String>> containing userIds of expense group members
     * Each entry contains expense_group_id, user_id
     *
     * @param apiKey         apiKey of the user calling this method
     * @param expenseGroupId id of the expense group
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null ||
     *                                  context == null || response == null}}
     * @pre {@code apiKey != null && expenseGroupId != null && context != null && response != null}
     * @post {@code APIResponse.data == userIds}
     */
    public static void getExpenseGroupMembers(String apiKey, String expenseGroupId, Context context,
                                              APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroupMembers.pre: apiKey or" +
                    "expenseGroupId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_group_id", expenseGroupId);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getExpenseGroupMembers",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Adds user to expense group.
     *
     * @param apiKey         apiKey of the user calling this method
     * @param userId         id of the user to be added
     * @param expenseGroupId id of the expense group
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || userId == null
     *                                  expenseGroupId || context == null || response == null}
     * @pre {@code apiKey != null && userId != null && expenseGroupId != null
     * && context != null && response != null}
     * @post {@code APIResponse.data in getExpenseGroupMembers(apiKey, expenseGroupId)}
     */
    public static void addToExpenseGroup(String apiKey, String userId, String expenseGroupId,
                                         Context context, APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || userId == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.addToExpenseGroup.pre:" +
                    "apiKey or userId or expenseGroupId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("expense_group_id", expenseGroupId);
        params.put("user_id", userId);
        params.put("api_key", apiKey);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "addToExpenseGroup",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Removes user from expense group
     *
     * @param apiKey         apiKey of the user calling this method
     * @param userId         id of the user to be removed
     * @param expenseGroupId id of the expense group
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null
     *                                  expenseGroupId || context == null || response == null}
     * @pre {@code apiKey != null && expenseGroupId != null
     * && context != null && response != null}
     * @post {@code APIResponse.data not in getExpenseGroupMembers(apiKey, expenseGroupId)}
     */
    public static void removeFromExpenseGroup(String apiKey, String userId, String expenseGroupId,
                                              Context context, APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || userId == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.removeFromExpenseGroup.pre:" +
                    "apiKey or userId or expenseGroupId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("expense_group_id", expenseGroupId);
        params.put("user_id", userId);
        params.put("api_key", apiKey);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "removeFromExpenseGroup",
                Request.Method.GET, params, null).run(context, response);


    }
}
