package com.dblappdev.app.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseService {

    /**
     * Creates expense and returns expense id of created expense.
     *
     * @param apiKey         apiKey of the user calling this method
     * @param userId         userId of person that made the expense (if null, caller is assumed)
     * @param title          title of the expense to be added
     * @param amount         amount of the expense to be added
     * @param picture        picture to be added
     * @param description    description of the expense to be added
     * @param expenseGroupId expense group id the transaction has to be added to
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || title == null ||
     *                                  amount == null ||
     *                                  description == null || expenseGroupId == null ||
     *                                  context == null || response == null}}
     * @pre {@code apiKey != null && title != null && amount != null &&
     * description != null && expenseGroupId != null && context != null &&
     * response != null}
     * @post {@code APIResponse.data in getExpenseGroupExpenses(apiKey, expenseGroupId)}
     */
    public static void createExpense(
            String apiKey,
            String userId,
            String title,
            String amount,
            Bitmap
            picture,
            String description,
            String expenseGroupId,
            Context context,
            APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || title == null || amount == null ||
                description == null || expenseGroupId == null) {
            throw new IllegalArgumentException("ExpenseService.createExpense.pre: apiKey or " +
                    "title or amount or description or expenseGroupId is null");
        }
        // Initialize base64 string of picture
        String pictureBase64;
        try {
            pictureBase64 = picture == null ? null : bitmapToBase64(picture);
        } catch (IllegalArgumentException e) {
            String errorMessage = "Picture too large, should be smaller than 10MB.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        // Set headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("title", title);
        headers.put("amount", amount);
        headers.put("description", description);
        headers.put("expense_group_id", expenseGroupId);
        headers.put("api_key", apiKey);
        if (userId != null) {
            headers.put("user_id", userId);
        }

        // Set parameters
        Map<String, String> params = new HashMap<>();
        if (pictureBase64 != null) {
            params.put("picture", pictureBase64);
        }

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "createExpense",
                Request.Method.POST, headers, params).run(context, response);

    }

    /**
     * Modifies expense entry
     *
     * @param apiKey         apiKey of the user calling this method
     * @param title          title of the expense to be modified
     * @param amount         amount of the expense to be modified
     * @param picture        picture to be modified
     * @param description    description of the expense to be modified
     * @param expenseGroupId expense group id the transaction has to be modified to
     * @param expenseId      expense id of the transaction to be modified
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response       contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || title == null ||
     *                                  amount == null || picture == null ||
     *                                  description == null || expenseGroupId == null}
     */
    public static void modifyExpense(String apiKey, String title, String amount, Bitmap
            picture, String description, String expenseGroupId, String expenseId, Context context,
                                     APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || title == null || amount == null ||
                description == null || expenseGroupId == null || context == null ||
                response == null) {
            throw new IllegalArgumentException("ExpenseService.modifyExpense.pre: apiKey or " +
                    "title or amount or description or expenseGroupId or expenseId is null");
        }

        // Initialize base64 string of picture
        String pictureBase64;
        try {
            pictureBase64 = picture == null ? null : bitmapToBase64(picture);
        } catch (IllegalArgumentException e) {
            String errorMessage = "Picture too large, should be smaller than 10MB.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        // Set headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("title", title);
        headers.put("amount", amount);
        headers.put("description", description);
        headers.put("expense_group_id", expenseGroupId);
        headers.put("expense_id", expenseId);
        headers.put("api_key", apiKey);

        // Set parameters
        Map<String, String> params = new HashMap<>();
        if (pictureBase64 != null) {
            params.put("picture", pictureBase64);
        }

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "modifyExpense",
                Request.Method.POST, headers, params).run(context, response);

    }

    /**
     * Removes expense from expense group
     *
     * @param apiKey    apiKey of the user calling this method
     * @param expenseId expense id of the expense
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     */
    public static void removeExpense(String apiKey, String expenseId, Context context,
                                     APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || expenseId == null || context == null ||
                response == null) {
            throw new IllegalArgumentException("ExpenseService.removeExpense.pre: apiKey or " +
                    "expenseId is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("expense_id", expenseId);
        params.put("api_key", apiKey);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "removeExpense",
                Request.Method.GET, params, null).run(context, response);
    }

    /**
     * Returns a Base64 string of Bitmap at 70% quality and checks size limit of 10MB
     *
     * @param picture picture to be converted
     * @return Base64 string of the picture
     * @throws IllegalArgumentException if size of picture greater than 10MB
     */
    static String bitmapToBase64(Bitmap picture) throws IllegalArgumentException {
        // Convert Bitmap to byte array
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 70, output);
        byte[] pictureByteArray = output.toByteArray();
        if (pictureByteArray.length > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Size of picture greater than 10MB");
        }
        return Base64.encodeToString(pictureByteArray, Base64.DEFAULT);
    }

    /**
     * Returns a Bitmap object of the given weblink
     *
     * @param weblink link to picture to be converted
     * @return Bitmap object of the picture
     */
    static Bitmap weblinkToBitmap(String weblink) throws IllegalStateException {
        // Convert link with website to bitmap object
        URL url = null;
        Bitmap bmp = null;
        try {
            url = new URL(weblink);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
        return bmp;
    }

    /**
     * Creates expenseIOU i.e. the amount of money each person in a expense group
     * owes the creator of an expense.
     *
     * @param apiKey    apiKey of the user calling this method
     * @param expenseId id of the expense
     * @param iouJson   JSONObject with userIds as keys, and amount owed as value
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null ||
     *                                  iouJson == null || context == null || response == null}}
     * @pre {@code apiKey != null && expenseId != null && iouJson != null && context != null &&
     * response != null}
     * @post {@code APIResponse.data == getOwedExpenses(apiKey, expenseId)}
     */
    public static void createExpenseIOU(String apiKey, String expenseId, JSONObject iouJson,
                                        Context context, APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || expenseId == null || iouJson == null) {
            throw new IllegalArgumentException("ExpenseService.createExpenseIOU.pre: apiKey or " +
                    "expenseId or iouJson is null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("expense_id", expenseId);
        params.put("api_key", apiKey);

        // Do request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "createExpenseIOU/" +
                iouJson.toString(),
                Request.Method.GET, params, null).run(context, response);
    }

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
