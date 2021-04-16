package com.dblappdev.app.api;

import com.android.volley.VolleyError;

/**
 * An object of this method has to be added in every API call.
 * In this method, the behavior in the case of a response form the backend is specified.
 * Also the behavior in the case of an error response is specified.
 *
 * @param <T> data type that the backend sends back
 *
 * Extra information:
 * The onErrorResponse method contains the error as well as the error message,
 * since retrieving the errorMessage form a VolleyError is highly non-trivial.
 */
public abstract class APIResponse<T> {

    /**
     * Callback method that is called when the backend responds correctly, with the data
     * send from the backend.
     *
     * @param data data retrieved form the backend
     */
    public abstract void onResponse(T data);

    /**
     * Callback method that is called when the backend responds incorrectly, i.e. HTTP response
     * code is not 200.
     *
     * @param error VolleyError that is returned by Volley.
     * @param errorMessage  String containing error message from the backend, if applicable.
     */
    public abstract void onErrorResponse(VolleyError error, String errorMessage);

}
