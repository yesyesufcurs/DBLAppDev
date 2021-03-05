package com.dblappdev.app.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class APIService {
    /**
     * Registers user in backend and returns apiKey
     *
     * @param username
     * @param password
     * @param email
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code username == null || password == null
     *                                  || email == null}
     * @throws IllegalArgumentException if {@code username.length() > 30 || password.length() < 6
     *                                  || email is valid}
     * @pre {@code username != null && password != null && email != null && username.length() <= 30
     * && password.length() >= 6 && email is valid}
     * @post {@code APIResponse.data == apiKey}
     */
    public static void register(String username, String password, String email, Context context,
                                APIResponse<String> response) {
        if (username == null || password == null || email == null || context == null ||
                response == null) {
            throw new IllegalArgumentException("APIService.register.pre: username, password or " +
                    "email is null.");
        }
        if (username.length() >= 30) {
            String errorMessage = "Username must be longer than 30 characters.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }
        if (password.length() < 6) {
            String errorMessage = "Password must be shorter than 6 characters.";
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

        AbstractAPIRequest<String> apiRequest = new AbstractAPIRequest<String>() {
            @Override
            protected Request<String> doAPIRequest(Response.Listener<String> responseListener,
                                                   Response.ErrorListener errorListener) {
                return new StringRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "register",
                        responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", username);
                        params.put("password", password);
                        params.put("email", email);

                        return params;
                    }

                };

            }


        };
        apiRequest.run(context, response);
    }


    /**
     * Logs user in and returns apiKey.
     *
     * @param username
     * @param password
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code username == null || password == null}
     * @pre {@code username != null && password != null}
     * @post {@code APIResponse.data == apiKey}
     */
    public static void login(String username, String password, Context context,
                             APIResponse<String> response) {
        if (username == null || password == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.login.pre: username or password" +
                    "is null");
        }

        AbstractAPIRequest<String> apiRequest = new AbstractAPIRequest<String>() {

            @Override
            protected Request<String> doAPIRequest(Response.Listener<String> responseListener,
                                                   Response.ErrorListener errorListener) {
                return new StringRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "login", responseListener,
                        errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", username);
                        params.put("password", password);

                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);
    }

    /**
     * Returns JSONArray containing expense groups a user is part of.
     * Each entry is of the form expense_group_id, expense_group_name, user_id of moderator.
     *
     * @param apiKey
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null}
     * @pre {@code apiKey != null}
     * @post {@code APIResponse.data == expenseGroups}
     */
    public static void getExpenseGroups(String apiKey, Context context, APIResponse<JSONArray> response) {
        if (apiKey == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroups.pre: apiKey is null");
        }

        AbstractAPIRequest<JSONArray> apiRequest = new AbstractAPIRequest<JSONArray>() {

            @Override
            protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray>
                                                              responseListener,
                                                      Response.ErrorListener errorListener) {
                return new JsonArrayRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "getExpenseGroups", null,
                        responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("api_key", apiKey);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);
    }

    /**
     * Returns JSONArray containing all available expense groups.
     * Each entry is of the form expense_group_id, expense_group_name, user_id of moderator.
     *
     * @param apiKey
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null}
     * @pre {@code apiKey != null}
     * @post {@code APIResponse.data == expenseGroups}
     */
    public static void getAllExpenseGroups(String apiKey, Context context, APIResponse<JSONArray> response) {
        if (apiKey == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.getAllExpenseGroups.pre: apiKey is" +
                    " null");
        }
        AbstractAPIRequest<JSONArray> apiRequest = new AbstractAPIRequest<JSONArray>() {

            @Override
            protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray>
                                                              responseListener,
                                                      Response.ErrorListener errorListener) {
                return new JsonArrayRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "getAllExpenseGroups", null,
                        responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("api_key", apiKey);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);
    }

    /**
     * Creates new expenseGroup and returns the assigned expense_group_id.
     *
     * @param apiKey
     * @param expenseGroupName
     * @param context          context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupName == null}
     * @pre {@code apiKey != null && expenseGroupName != null}
     * @post {@code APIResponse.data == expenseGroups}
     */
    public static void createExpenseGroup(String apiKey, String expenseGroupName, Context
            context,
                                          APIResponse<String> response) {
        if (apiKey == null || expenseGroupName == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.createExpenseGroup.pre: apiKey or " +
                    "expenseGroupName is null");
        }
        AbstractAPIRequest<String> apiRequest = new AbstractAPIRequest<String>() {
            @Override
            protected Request<String> doAPIRequest(Response.Listener<String> responseListener,
                                                   Response.ErrorListener errorListener) {
                return new StringRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "createExpenseGroup",
                        responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("expense_group_name", expenseGroupName);
                        params.put("api_key", apiKey);

                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);
    }

    /**
     * Returns JSONArray containing userIds of expense group members
     *
     * @param apiKey
     * @param expenseGroupId
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null}
     * @pre {@code apiKey != null && expenseGroupId != null}
     * @post {@code APIResponse.data == userIds}
     */
    public static void getExpenseGroupMembers(String apiKey, String expenseGroupId, Context context,
                                              APIResponse<JSONArray> response) {
        if (apiKey == null || expenseGroupId == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroupMembers.pre: apiKey or" +
                    "expenseGroupId is null");
        }
        AbstractAPIRequest<JSONArray> apiRequest = new AbstractAPIRequest<JSONArray>() {

            @Override
            protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray>
                                                              responseListener,
                                                      Response.ErrorListener errorListener) {
                return new JsonArrayRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "getExpenseGroupMembers", null,
                        responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("api_key", apiKey);
                        params.put("expense_group_id", expenseGroupId);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);
    }

    /**
     * Adds user to expense group.
     *
     * @param apiKey
     * @param userId
     * @param expenseGroupId
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || userId == null
     *                                  expenseGroupId}
     * @pre {@code apiKey != null && userId != null && expenseGroupId != null}
     * @post {@code APIResponse.data in getExpenseGroupMembers(apiKey, expenseGroupId)}
     */
    public static void addToExpenseGroup(String apiKey, String userId, String expenseGroupId,
                                         Context context, APIResponse<String> response) {
        if (apiKey == null || userId == null || expenseGroupId == null || context == null ||
                response == null) {
            throw new IllegalArgumentException("APIService.addToExpenseGroup.pre:" +
                    "apiKey or userId or expenseGroupId is null");
        }
        AbstractAPIRequest<String> apiRequest = new AbstractAPIRequest<String>() {
            @Override
            protected Request<String> doAPIRequest(Response.Listener<String> responseListener,
                                                   Response.ErrorListener errorListener) {
                return new StringRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "addToExpenseGroup",
                        responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("expense_group_id", expenseGroupId);
                        params.put("user_id", userId);
                        params.put("api_key", apiKey);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);
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
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || title == null ||
     *                                  amount == null || picture == null ||
     *                                  description == null || expenseGroupId == null}
     * @pre {@code apiKey != null && title != null && amount != null &&
     * picture != null && description != null && expenseGroupId != null}
     * @post {@code APIResponse.data in getExpenseGroupExpenses(apiKey, expenseGroupId)}
     */
    public static void createExpense(String apiKey, String title, String amount, Bitmap
            picture, String description, String expenseGroupId, Context context,
                                     APIResponse<String> response) {
        if (apiKey == null || title == null || amount == null || picture == null ||
                description == null || expenseGroupId == null || context == null ||
                response == null) {
            throw new IllegalArgumentException("APIService.createExpense.pre: apiKey or " +
                    "title or amount or picture or description or expenseGroupId is null");
        }

        String pictureBase64;
        try {
            pictureBase64 = bitmapToBase64(picture);
        } catch(IllegalArgumentException e) {
            String errorMessage = "Picture too large, should be smaller than 10MB.";
            response.onErrorResponse(new VolleyError(errorMessage), errorMessage);
            return;
        }

        AbstractAPIRequest<String> apiRequest = new AbstractAPIRequest<String>() {
            @Override
            protected Request<String> doAPIRequest(Response.Listener<String> responseListener,
                                                   Response.ErrorListener errorListener) {
                return new StringRequest(Request.Method.POST,
                        AbstractAPIRequest.getAPIUrl() + "createExpense",
                        responseListener, errorListener) {
                    // Add correct headers
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("title", title);
                        params.put("amount", amount);
                        params.put("description", description);
                        params.put("expense_group_id", expenseGroupId);
                        params.put("api_key", apiKey);
                        return params;
                    }

                    // Add picture as a string
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("picture", pictureBase64);

                        return params;
                    }

                };
            }
        };
        apiRequest.run(context, response);


    }

    /**
     * Returns a Base64 string of Bitmap at 70% quality and checks size limit of 10MB
     * @param picture picture to be converted
     * @throws IllegalArgumentException if size of picture greater than 10MB
     * @return Base64 string of the picture
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
     * Creates expenseIOU i.e. the amount of money each person in a expense group
     * owes the creator of an expense.
     *
     * @param apiKey
     * @param expenseId
     * @param iouJson   JSONObject with userIds as keys, and amount owed as value
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null ||
     *                                  iouJson == null}
     * @pre {@code apiKey != null && expenseId != null && iouJson != null}
     * @post {@code APIResponse.data == getOwedExpenses(apiKey, expenseId)}
     */
    public static void createExpenseIOU(String apiKey, String expenseId, JSONObject iouJson,
                                        Context context, APIResponse<String> response) {
        if (apiKey == null || expenseId == null || iouJson == null || context == null ||
                response == null) {
            throw new IllegalArgumentException("APIService.createExpenseIOU.pre: apiKey or " +
                    "expenseId or iouJson is null");
        }

        AbstractAPIRequest<String> apiRequest = new AbstractAPIRequest<String>() {
            @Override
            protected Request<String> doAPIRequest(Response.Listener<String> responseListener,
                                                   Response.ErrorListener errorListener) {
                return new StringRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "createExpenseIOU/" + iouJson.toString(),
                        responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("expense_id", expenseId);
                        params.put("api_key", apiKey);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);

    }

    /**
     * Returns JSONArray containing all expense details of all expenses in an expense group.
     *
     * @param apiKey
     * @param expenseGroupId
     * @param context        context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseGroupId == null}
     * @pre {@code apiKey != null && expenseGroupId != null}
     * @post {@code APIResponse.data == expenseGroupExpenses : expenseGroupExpenses.expenseGroupId
     * == expenseGroupId}
     */
    public static void getExpenseGroupExpenses(String apiKey, String expenseGroupId,
                                               Context context, APIResponse<JSONArray> response) {
        if (apiKey == null || expenseGroupId == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.getExpenseGroupExpenses.pre: " +
                    "apiKey or expenseGroupId is null");
        }

        AbstractAPIRequest<JSONArray> apiRequest = new AbstractAPIRequest<JSONArray>() {

            @Override
            protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray>
                                                              responseListener,
                                                      Response.ErrorListener errorListener) {
                return new JsonArrayRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "getExpenseGroupExpenses",
                        null, responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("api_key", apiKey);
                        params.put("expense_group_id", expenseGroupId);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);

    }

    /**
     * Returns JSONArray containing expense details of all expenses created by the user that makes the request
     *
     * @param apiKey
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null}
     * @pre {@code apiKey != null}
     * @post {@code APIResponse.data == userExpenses : userExpenses.expenseGroupId == apiKey.user}
     */
    public static void getUsersExpenses(String apiKey, Context context,
                                        APIResponse<JSONArray> response) {
        if (apiKey == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.getUserExpenses.pre: apiKey is null");
        }

        AbstractAPIRequest<JSONArray> apiRequest = new AbstractAPIRequest<JSONArray>() {

            @Override
            protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray>
                                                              responseListener,
                                                      Response.ErrorListener errorListener) {
                return new JsonArrayRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "getUsersExpenses",
                        null, responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("api_key", apiKey);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);

    }

    /**
     * Returns JSONArray containing all expenses where the user owes someone else money
     *
     * @param apiKey
     * @param context  context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null}
     * @pre {@code apiKey != null}
     * @post {@code APIResponse.data == userOwedExpenses : \forall i; userOwedExpenses.ids().has(i);
     * getOwedExpenses(userOwedExpenses[i]).ower() == apiKey.user}
     */
    public static void getUserOwedExpenses(String apiKey, Context context,
                                           APIResponse<JSONArray> response) {
        if (apiKey == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.getUserExpenses.pre: apiKey is null");
        }
        AbstractAPIRequest<JSONArray> apiRequest = new AbstractAPIRequest<JSONArray>() {

            @Override
            protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray>
                                                              responseListener,
                                                      Response.ErrorListener errorListener) {
                return new JsonArrayRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "getUserOwedExpenses",
                        null, responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("api_key", apiKey);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);
    }


    /**
     * Returns JSONArray containing the details of an expense given an expenseId
     *
     * @param apiKey
     * @param expenseId
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null}
     * @pre {@code apiKey != null && expenseId != null}
     * @post {@code APIResponse.data == expenseDetails : expenseDetails.id() == expenseId}
     */
    public static void getExpenseDetails(String apiKey, String expenseId, Context context,
                                         APIResponse<JSONArray> response) {
        if (apiKey == null || expenseId == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.getExpenseDetails.pre: " +
                    "apiKey or expenseId is null.");
        }
        AbstractAPIRequest<JSONArray> apiRequest = new AbstractAPIRequest<JSONArray>() {

            @Override
            protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray>
                                                              responseListener,
                                                      Response.ErrorListener errorListener) {
                return new JsonArrayRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "getExpenseDetails",
                        null, responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("api_key", apiKey);
                        params.put("expense_id", expenseId);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);

    }

    /**
     * Returns how much each person owes the expense creator given expenseId
     *
     * @param apiKey
     * @param expenseId
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     * @throws IllegalArgumentException if {@code apiKey == null || expenseId == null}
     * @pre {@code apiKey != null && expenseId != null}
     * @post {@code APIResponse.data == owedExpenses: owedExpenses.expenseId() == expenseId}
     */
    public static void getOwedExpenses(String apiKey, String expenseId, Context context,
                                       APIResponse<JSONArray> response) {
        if (apiKey == null || expenseId == null || context == null || response == null) {
            throw new IllegalArgumentException("APIService.getOwedExpenses.pre: " +
                    "apiKey or expenseId is null.");
        }

        AbstractAPIRequest<JSONArray> apiRequest = new AbstractAPIRequest<JSONArray>() {

            @Override
            protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray>
                                                              responseListener,
                                                      Response.ErrorListener errorListener) {
                return new JsonArrayRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "getOwedExpenses",
                        null, responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("api_key", apiKey);
                        params.put("expense_id", expenseId);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);

    }

    /**
     * Toggles the Paid value in the given accured for the given userId expense.
     * @param apiKey
     * @param expenseId
     * @param userId
     * @param context   context of request, often AppActivity (instance of calling object)
     * @param response  contains a callback method that is called on (un)successful request.
     */
    public static void setUserPaidExpense(String apiKey, String expenseId, String userId,
                                          Context context, APIResponse<String> response) {
        if (apiKey == null || expenseId == null || userId == null || context == null ||
                response == null) {
            throw new IllegalArgumentException("APIService.setUserPaidExpense.pre: " +
                    "apiKey, expenseId or userId is null.");
        }

        AbstractAPIRequest<String> apiRequest = new AbstractAPIRequest<String>() {
            @Override
            protected Request<String> doAPIRequest(Response.Listener<String> responseListener,
                                                   Response.ErrorListener errorListener) {
                return new StringRequest(Request.Method.GET,
                        AbstractAPIRequest.getAPIUrl() + "setUserPaidExpense",
                        responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("expense_id", expenseId);
                        params.put("api_key", apiKey);
                        params.put("user_id", userId);
                        return params;
                    }
                };
            }
        };
        apiRequest.run(context, response);

    }


}
