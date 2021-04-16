package com.dblappdev.app.api;

import android.content.Context;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseServiceQueries {
    /**
     * Returns List<Map<String, String>> containing how much each person owes the creator of the
     * expense.
     * Each entry contains: expense_id, user_id, amount, paid
     *
     * @param apiKey    apiKey of the user calling this method
     * @param expenseId id of the expense
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @pre {@code apiKey != null && expenseId != null && context != null &&
     * response != null}
     * @post {@code APIResponse.data == getOwedExpenses(apiKey, expenseId)}
     */
    public static void getExpenseIOU(String apiKey, String expenseId, Context context,
                                     APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseId == null) {
            throw new IllegalArgumentException("ExpenseService.getExpenseIOU.pre: apiKey or " +
                    "expenseId or iouJson is null");
        }
        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("expense_id", expenseId);
        params.put("api_key", apiKey);

        // Do request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getExpenseIOU",
                Request.Method.GET, params, null).run(context, response);
    }

    /**
     * Returns List<Map<String, String>> containing all expense details of all expenses in an
     * expense group.
     * Each entry contains: id, title, amount, content, expense_group_id, user_id, timestamp.
     *
     * @param apiKey         apiKey of the user calling this method
     * @param expenseGroupId id of the expense group
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null
     *                                  || context == null || response == null}}
     * @pre {@code apiKey != null && expenseGroupId != null && context != null && response != null}
     * @post {@code APIResponse.data == expenseGroupExpenses : expenseGroupExpenses.expenseGroupId
     * == expenseGroupId}
     */
    public static void getExpenseGroupExpenses(String apiKey, String expenseGroupId,
                                               Context context,
                                               APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("ExpenseService.getExpenseGroupExpenses.pre: " +
                    "apiKey or expenseGroupId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_group_id", expenseGroupId);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getExpenseGroupExpenses",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Returns List<Map<String, String>> containing the details of an expense given an expenseId
     * Each entry contains: id, user_id, title, amount, content, expense_group_id, timestamp.
     *
     * @param apiKey    apiKey of the user calling this method
     * @param expenseId id of the expense
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null
     *                                  || context == null || response == null}}
     * @pre {@code apiKey != null && expenseId != null && context != null && response != null}
     * @post {@code APIResponse.data == expenseDetails : expenseDetails.id() == expenseId}
     */
    public static void getExpenseDetails(String apiKey, String expenseId, Context context,
                                         APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseId == null) {
            throw new IllegalArgumentException("ExpenseService.getExpenseDetails.pre: " +
                    "apiKey or expenseId is null.");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_id", expenseId);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getExpenseDetails",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Returns List<Map<String, String>> containing each person the user owes money to
     * and the amount the user owes, so the user (caller) is in debt.
     * Each entry contains: user_id, amount.
     *
     * @param apiKey         apiKey of the user calling this method
     * @param expenseGroupId expense group id of which expenses should be considered
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null ||
     *                                  context == null || response == null}}
     * @pre {@code apiKey != null && expenseGroupId != null && context != null && response != null}
     * @post {@code APIResponse.data == sum(userOwedExpenses.amount) grouped by user }
     */
    public static void getUserOwedTotal(String apiKey, String expenseGroupId, Context context,
                                        APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("ExpenseService.getUserOwedTotal.pre: apiKey" +
                    " or expenseGroupId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_group_id", expenseGroupId);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getUserOwedTotal",
                Request.Method.GET, params, null).run(context, response);
    }
}
