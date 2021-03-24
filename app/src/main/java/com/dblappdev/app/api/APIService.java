package com.dblappdev.app.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dblappdev.app.gregservice.GregService.isASCII;

public abstract class APIService {
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
            throw new IllegalArgumentException("APIService.register.pre: username, password or " +
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

        // Invoke Request
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "register", Request.Method.GET,
                headers, null).run(context, response);
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
            throw new IllegalArgumentException("APIService.login.pre: username or password" +
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
     * Returns List<Map<String, String>> containing all available expense groups.
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
    public static void getAllExpenseGroups(String apiKey, Context context,
                                           APIResponse<List<Map<String, String>>> response) {
        // Check preconditions
        if (apiKey == null) {
            throw new IllegalArgumentException("APIService.getAllExpenseGroups.pre: apiKey is" +
                    " null");
        }

        // Set headers
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);

        // Do API Request
        new JSONAPIRequest(AbstractAPIRequest.getAPIUrl() + "getAllExpenseGroups",
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
    public static void createExpense(String apiKey, String userId, String title, String amount, Bitmap
            picture, String description, String expenseGroupId, Context context,
                                     APIResponse<String> response) {
        // Check preconditions
        if (apiKey == null || title == null || amount == null ||
                description == null || expenseGroupId == null) {
            throw new IllegalArgumentException("APIService.createExpense.pre: apiKey or " +
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
            throw new IllegalArgumentException("APIService.modifyExpense.pre: apiKey or " +
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
            throw new IllegalArgumentException("APIService.removeExpense.pre: apiKey or " +
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
    private static String bitmapToBase64(Bitmap picture) throws IllegalArgumentException {
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
    private static Bitmap weblinkToBitmap(String weblink) throws IllegalStateException {
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
    public static void getExpensePicture(String apiKey, String expenseId, Context context,
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
            picture = weblinkToBitmap(url);
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
    public static void detectText(String apiKey, Bitmap picture, Context context,
                                  APIResponse<String> response) {
        if (apiKey == null || picture == null) {
            throw new IllegalArgumentException("APIService.getPictureText.pre: " +
                    "apiKey or picture is null");
        }

        // Initialize base64 string of picture
        String pictureBase64;
        try {
            pictureBase64 = bitmapToBase64(picture);
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
        new StringAPIRequest(AbstractAPIRequest.getAPIUrl() + "detectText",
                Request.Method.POST, headers, params).run(context, response);
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
            throw new IllegalArgumentException("APIService.createExpenseIOU.pre: apiKey or " +
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
            throw new IllegalArgumentException("APIService.getExpenseIOU.pre: apiKey or " +
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
    public static void searchExpense(String apiKey, String expenseGroupId, String query, Context context,
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
            throw new IllegalArgumentException("APIService.getExpenseGroupExpenses.pre: " +
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
            throw new IllegalArgumentException("APIService.getUserOwedTotal.pre: apiKey" +
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

    /**
     * Returns List<Map<String, String>> containing each person that owes money to the user (caller)
     * and the amount the person owes, so the user (caller) should get money.
     * Each entry contains: user_id, amount.
     *
     *  @param apiKey         apiKey of the user calling this method
     *  @param expenseGroupId expense group id of which expenses should be considered
     *  @param context        context of request, often AppActivity (instance of calling object)
     *  @param response       contains a callback method that is called on (un)successful request.
     *  @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null
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
            throw new IllegalArgumentException("APIService.getExpenseDetails.pre: " +
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
