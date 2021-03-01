package com.dblappdev.app.api;

import android.content.Context;

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
 */
public abstract class AbstractAPIRequest<T> {
    private final static String api_url = "http://10.0.2.2:5000/";
    protected Response.Listener<T> responseListener;
    protected Response.ErrorListener errorListener;
    protected String errorMessage = "Generic error message.";

    /**
     * Primitive method to be overwritten by implementer.
     * Returns the VolleyRequest to be executed.
     *
     * @param responseListener
     * @param errorListener
     * @return
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
     * Template method to be executed to run the API request.
     *
     * @param context
     * @param apiResponse
     */
    public void run(Context context, APIResponse<T> apiResponse) {
        responseListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                apiResponse.onResponse(response);
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseNetworkData = new String(error.networkResponse.data,
                            "utf-8");
                    errorMessage = new JSONObject(responseNetworkData).optString("text");
                } catch (Exception e) {
                    errorMessage = error.getMessage();
                }
                apiResponse.onErrorResponse(error, errorMessage);
            }
        };

        Request<T> request = doAPIRequest(responseListener, errorListener);
        RequestHandler.getInstance(context).addToRequestQueue(request);

    }

    ;

}
