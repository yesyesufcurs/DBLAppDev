package com.dblappdev.app.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Abstract API Request using template design pattern.
 * Overwrite doAPIRequest that returns the VolleyRequest to be executed.
 *
 * @param <T> Object type that is returned by VolleyRequest
 * @param <K> Object type that is expected by APIResponse
 */
public abstract class AbstractAPIRequest<T, K> {
    // For local testing
//    private final static String api_url = "http://10.0.2.2:5000/";
    // For deployment
    private final static String api_url = "http://94.130.144.25:5000/";
    protected Response.Listener<T> responseListener;
    protected Response.ErrorListener errorListener;
    protected String errorMessage = "Something went wrong.";

    /**
     * Primitive method to be overwritten by implementer.
     * Returns the VolleyRequest to be executed.
     *
     * @param responseListener Volley responseListener
     * @param errorListener    Volley errorListener
     * @return Volley Request that must be executed
     */
    protected abstract Request<T> doAPIRequest(Response.Listener<T> responseListener,
                                               Response.ErrorListener errorListener);

    /**
     * Returns api_url field value.
     *
     * @return api_url field value.
     */
    public static String getAPIUrl() {
        return api_url;
    }

    /**
     * Converts data from VolleyRequest to data expected by APIResponse
     *
     * @param data data returned by VolleyRequest
     * @return converted data object in type K
     * @throws IllegalStateException if data cannot be converted
     */
    protected abstract K convertData(T data);


    /**
     * Template method to be executed to run the API request.
     *
     * @param context     Context of caller
     * @param apiResponse APIResponse of caller
     * @throws IllegalArgumentException if {@code context == null || apiResponse == null}
     * @pre {@code context != null && apiResponse != null}
     */
    public void run(Context context, APIResponse<K> apiResponse) {
        if (context == null) {
            throw new IllegalArgumentException("AbstractAPIRequest.run.pre: context is null.");
        }
        if (apiResponse == null) {
            throw new IllegalArgumentException("AbstractAPIRequest.run.pre: apiResponse is null.");
        }

        responseListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                K convertedData;
                try {
                    convertedData = convertData(response);
                } catch (IllegalStateException e) {
                    apiResponse.onErrorResponse(new VolleyError(e.getMessage()), e.getMessage());
                    return;
                }
                apiResponse.onResponse(convertedData);
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Try to retrieve server error response.
                try {
                    String responseNetworkData = new String(error.networkResponse.data,
                            "utf-8");
                    errorMessage = new JSONObject(responseNetworkData).optString("text");
                } catch (Exception e) {
                }
                // Check if error was a TimeoutError
                if (error.getClass().getSimpleName().equals("TimeoutError")) {
                    errorMessage = "Cannot establish connection to server.";
                }
                apiResponse.onErrorResponse(error, errorMessage);
            }
        };

        Request<T> request = doAPIRequest(responseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(
                12000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(context).addToRequestQueue(request);

    }


}
