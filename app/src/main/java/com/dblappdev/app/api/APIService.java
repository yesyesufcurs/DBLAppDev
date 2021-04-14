package com.dblappdev.app.api;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class APIService {


    /**
     * Returns a bitmap object of the picture of the expense.
     *
     * @param apiKey    apiKey of the user calling this method
     * @param expenseId expense id of the expense
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null ||
     *                                  context == null || response == null}
     * @pre {@code apiKey != null && expenseId != null && context != null &&
     * response != null}
     * @post {\result == Bitmap object of picture}
     */
    public static void getExpensePicture(
            String apiKey,
            String expenseId,
            Context context,
            APIResponse<Bitmap> response) {
        if (apiKey == null || expenseId == null) {
            throw new IllegalArgumentException("APIService.getExpensePicture.pre: apiKey or " +
                    "expenseId is null");
        }
        String url = AbstractAPIRequest.getAPIUrl() + "getExpensePicture/" + expenseId + "/" +
                apiKey;
        // Get image from URL.
        Bitmap picture = null;
        try {
            picture = ExpenseService.weblinkToBitmap(url);
        } catch (IllegalStateException e) {
            String error = "Cannot retrieve picture";
            response.onErrorResponse(new VolleyError(error), error);
            return;
        }
        response.onResponse(picture);
    }

    /**
     * Returns the detected text from a picture
     *
     * @param apiKey   apiKey of the user calling this method
     * @param picture  bitmap of the picture of which the text must be recognized
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response contains a callback method that is called on (un)successful request.
     */
    public static void detectText(
            String apiKey,
            Bitmap picture,
            Context context,
            APIResponse<String> response) {
        if (apiKey == null || picture == null) {
            throw new IllegalArgumentException("APIService.getPictureText.pre: " +
                    "apiKey or picture is null");
        }

        // Initialize base64 string of picture
        String pictureBase64;
        try {
            pictureBase64 = ExpenseService.bitmapToBase64(picture);
        } catch (IllegalArgumentException e) {
            String errorMessage = "Picture too large, should be smaller than 10MB.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        // Set headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("api_key", apiKey);

        // Set params
        Map<String, String> params = new HashMap<String, String>();
        params.put("picture", pictureBase64);

        // Do API Request
        new StringAPIRequest(
                AbstractAPIRequest.getAPIUrl() + "detectText",
                Request.Method.POST, headers, params).run(context, response);
    }

    /**
     * Removes owed expense of user with userId in expense with expenseId
     *
     * @param apiKey    apiKey of the user calling this method
     * @param expenseId id of the expense
     * @param userId    id of the user to be removed from owed expenses
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null ||
     *                                  userId == null || context == null || response == null}
     * @pre {@code apiKey != null && expenseId != null && userId != null && context != null
     * && response != null}
     * @post {@code AccuredExpenses.key(userId, expenseId) does not exist}
     */
    public static void removeOwedExpense(String apiKey, String expenseId, String userId,
                                         Context context, APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || expenseId == null || userId == null) {
            throw new IllegalArgumentException("APIService.removeOwedExpense.pre: apiKey or " +
                    "expenseId or userId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("expense_id", expenseId);
        params.put("user_id", userId);
        params.put("api_key", apiKey);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "removeOwedExpense",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Returns List<Map<String, String>> containing all expense details of the expenses found
     * by the query.
     * Each entry contains: id, title, amount, content, expense_group_id, user_id
     *
     * @param apiKey         apiKey of the user calling this method
     * @param expenseGroupId id of the expense group
     * @param query          query typed in by the caller
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null
     *                                  || query == null || context == null || response == null}}
     * @pre {@code apiKey != null && expenseGroupId != null && query != null &&
     * context != null && response != null}
     * @post {@code APIResponse.data == expenseGroupExpenses.search(query) :
     * expenseGroupExpenses.expenseGroupId == expenseGroupId}
     */
    public static void searchExpense(
            String apiKey,
            String expenseGroupId,
            String query,
            Context context,
            APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseGroupId == null || query == null) {
            throw new IllegalArgumentException("APIService.searchExpense.pre: apiKey or " +
                    "expenseGroupId or query is null");
        }

        // Check query length
        if (query.length() > 100) {
            String error = "Query should be less than 100 characters.";
            response.onErrorResponse(new VolleyError(error), error);
            return;
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_group_id", expenseGroupId);
        params.put("query", query);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "searchExpense",
                Request.Method.GET, params, null).run(context, response);
    }

    /**
     * Returns List<Map<String, String>> containing expense details of all expenses created by
     * the user that makes the request
     * Each entry contains: id, title, amount, content, expense_group_id, user_id, timestamp.
     *
     * @param apiKey   apiKey of the user calling this method
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || context == null ||
     *                                  response == null}}
     * @pre {@code apiKey != null && context != null && response != null}
     * @post {@code APIResponse.data == userExpenses : userExpenses.expenseGroupId == apiKey.user}
     */
    public static void getUsersExpenses(String apiKey, Context context,
                                        APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getUserExpenses.pre: apiKey is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getUsersExpenses",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Returns List<Map<String, String>> containing all expenses where the user owes someone else
     * money
     * Each entry contains: id, title, amount, content, expense_group_id, user_id, timestamp.
     *
     * @param apiKey   apiKey of the user calling this method
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || context == null ||
     *                                  response == null}}
     * @pre {@code apiKey != null && context != null && response != null}
     * @post {@code APIResponse.data == userOwedExpenses : \forall i; userOwedExpenses.ids().has(i);
     * getOwedExpenses(userOwedExpenses[i]).ower() == apiKey.user}
     */
    public static void getUserOwedExpenses(String apiKey, Context context,
                                           APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getUserExpenses.pre: apiKey is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getUserOwedExpenses",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Returns List<Map<String, String>> containing each person that owes money to the user (caller)
     * and the amount the person owes, so the user (caller) should get money.
     * Each entry contains: user_id, amount.
     *
     * @param apiKey         apiKey of the user calling this method
     * @param expenseGroupId expense group id of which expenses should be considered
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null
     *                                  context == null || response == null}}
     * @pre {@code apiKey != null && expenseGroupId != null && context != null && response != null}
     * @post {@code APIResponse.data == sum(userDebitedExpenses.amount) grouped by user }
     */
    public static void getUserDebitTotal(String apiKey, String expenseGroupId, Context context,
                                         APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.getUserDebitTotal.pre: apiKey" +
                    " or expenseGroupId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_group_id", expenseGroupId);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getUserDebitTotal",
                Request.Method.GET, params, null).run(context, response);

    }


    /**
     * Returns how much each person owes the expense creator given expenseId
     * Each entry contains: amount, expense_id, paid (0 or 1), user_id.
     *
     * @param apiKey    apiKey of the user calling this method
     * @param expenseId id of the expense
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null
     *                                  || context == null || response == null}}
     * @pre {@code apiKey != null && expenseId != null && context != null && response != null}
     * @post {@code APIResponse.data == owedExpenses: owedExpenses.expenseId() == expenseId}
     */
    public static void getOwedExpenses(String apiKey, String expenseId, Context context,
                                       APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null || expenseId == null) {
            throw new IllegalArgumentException("APIService.getOwedExpenses.pre: " +
                    "apiKey or expenseId is null.");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("expense_id", expenseId);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getOwedExpenses",
                Request.Method.GET, params, null).run(context, response);

    }

    /**
     * Toggles the Paid value in the given accured for the given userId expense.
     *
     * @param apiKey    apiKey of the user calling this method
     * @param expenseId id of the expense where paid has to be toggled
     * @param userId    id of the user that has to be toggled
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @pre {@code apiKey != null && expenseId != null &&
     * userId != null && context != null && response != null}
     * @post {@code AccuredExpenses.key(userId, expenseId).paid ==
     * !\old(AccuredExpenses.key(userId, expenseId).paid) }
     */
    public static void setUserPaidExpense(String apiKey, String expenseId, String userId,
                                          Context context, APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || expenseId == null || userId == null) {
            throw new IllegalArgumentException("APIService.setUserPaidExpense.pre: " +
                    "apiKey, expenseId or userId is null.");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("expense_id", expenseId);
        params.put("api_key", apiKey);
        params.put("user_id", userId);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "setUserPaidExpense",
                Request.Method.GET, params, null).run(context, response);
    }

}
