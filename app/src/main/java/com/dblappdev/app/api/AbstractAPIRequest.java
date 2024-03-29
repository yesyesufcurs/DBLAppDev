package com.dblappdev.app.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;


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
    private Response.Listener<T> responseListener;
    private Response.ErrorListener errorListener;
    private String errorMessage = "Something went wrong.";
    // Semaphore preventing multiple requests to API.
    private static boolean isRequestHappening = false;
    // Counter, counting retries of a request
    static int retryCount = 0;

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
     * Template method to be executed to run the API request, with default behaviour.
     *
     * @param context     Context of caller
     * @param apiResponse APIResponse of caller
     * @throws IllegalArgumentException if {@code context == null || apiResponse == null}
     * @pre {@code context != null && apiResponse != null}
     */
    public void run(Context context, APIResponse<K> apiResponse) {
        run(context, apiResponse, null);
    }

    /**
     * Template method to be executed to run the API request.
     *
     * @param context     Context of caller
     * @param apiResponse APIResponse of caller
     * @param onEmulatorBugResponse responseCode when there is an emulator bug as explained here
     *                              https://github.com/google/volley/issues/92
     * @throws IllegalArgumentException if {@code context == null || apiResponse == null}
     * @pre {@code context != null && apiResponse != null}
     */
    public void run(Context context, APIResponse<K> apiResponse,
                    APIResponse<K> onEmulatorBugResponse) {
        if (context == null) {
            throw new IllegalArgumentException("AbstractAPIRequest.run.pre: context is null.");
        }
        if (apiResponse == null) {
            throw new IllegalArgumentException("AbstractAPIRequest.run.pre: apiResponse is null.");
        }
        if (isRequestHappening) {
            // Do not do anything.
            return;
        }
        // Set semaphore
        isRequestHappening = true;

        responseListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                // Update semaphore
                isRequestHappening = false;
                // Successful request, so reset retry counter
                retryCount = 0;
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
                // Update semaphore
                isRequestHappening = false;
                // Try to retrieve server error response.
                try {
                    String responseNetworkData = new String(error.networkResponse.data,
                            "utf-8");
                    errorMessage = new JSONObject(responseNetworkData).optString("text");
                } catch (Exception e) {
                    // Not caused by a backend error, retry request if retryCount is under 10 times
                    if (retryCount < 10 && onEmulatorBugResponse == null) {
                        retryCount++;
                        run(context, apiResponse, null);
                        return;
                    } else if (onEmulatorBugResponse != null) {
                        onEmulatorBugResponse.onResponse(null);
                        return;
                    }
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
