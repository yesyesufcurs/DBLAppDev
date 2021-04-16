package com.dblappdev.app.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExpenseServiceCommands {

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
            Bitmap picture,
            String description,
            String expenseGroupId,
            Context context,
            APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || title == null || amount == null ||
                description == null || expenseGroupId == null) {
            throw new IllegalArgumentException("ExpenseService.createExpense.pre: apiKey or " +
                    "title or amount or description or expenseGroupId is null.");
        }

        // Initialize base64 string of picture
        String pictureBase64 = picture == null ? null : bitmapToBase64(picture, response);

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
        String pictureBase64 = picture == null ? null : bitmapToBase64(picture, response);

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
     * @param picture  picture to be converted
     * @param response when an error needs to be thrown, throw it to repsone
     * @return Base64 string of the picture
     * @throws IllegalArgumentException if size of picture greater than 10MB
     */
    static String bitmapToBase64(Bitmap picture, APIResponse<String> response) {
        // Convert Bitmap to byte array
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 70, output);
        byte[] pictureByteArray = output.toByteArray();
        if (pictureByteArray.length > 10 * 1024 * 1024) {
            String errorMessage = "Picture too large, should be smaller than 10MB.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return null;
        }
        return Base64.encodeToString(pictureByteArray, Base64.DEFAULT);
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

}
