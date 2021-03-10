package com.dblappdev.app.api;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StringAPIRequest extends AbstractAPIRequest<String, String> {

    String url;                     // String containing API url
    int method;                     // Request method
    Map<String, String> headers;    // Map<String, String> containing headers for request
    Map<String, String> params;     // Map<String, String> containing params

    /**
     * Constructor of StringAPIRequest
     *
     * @param url       API url endpoint
     * @param method    request method (0 = GET, 1 = POST)
     * @param headers   headers for API Request
     * @param params    parameters for API Request, if needed
     * @throws IllegalArgumentException if {@code headers == null || url == null}
     * @pre {url != null && headers != null}
     */
    StringAPIRequest(String url, int method , Map<String, String> headers, Map<String, String> params){
        if (url == null || headers == null) {
            throw new IllegalArgumentException("StringAPIRequest.pre: url or headers is null.");
        }
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.params = params;
    }

    @Override
    protected Request<String> doAPIRequest(Response.Listener<String> responseListener,
                                           Response.ErrorListener errorListener){
        return new StringRequest(method, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params != null) {
                    return params;
                } else{
                    return new HashMap<String, String>();
                }
            }
        };
    }
}
