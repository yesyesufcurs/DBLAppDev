package com.dblappdev.app.api;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that contains a default implementation for an JSONAPIRequest where the
 * user expects a List<Map<String, String>> object.
 *
 */
public class JSONAPIRequest extends AbstractAPIRequest<JSONArray, List<Map<String, String>>> {

    String url;                     // String containing API url
    int method;                     // Request method
    Map<String, String> headers;    // Map<String, String> containing headers for request
    Map<String, String> params;     // Map<String, String> containing parameters for request

    /**
     * Constructor of JSONAPIRequest
     *
     * @param url     API url endpoint
     * @param method  request method (0 = GET, 1 = POST)
     * @param headers headers for API Request
     * @param params  parameters for API Request, if needed
     * @throws IllegalArgumentException if {@code headers == null || url == null}
     * @pre {url != null && headers != null}
     */
    public JSONAPIRequest(String url, int method, Map<String, String> headers,
                   Map<String, String> params) {
        // Check preconditions
        if (url == null || headers == null) {
            throw new IllegalArgumentException("StringAPIRequest.pre: url or headers is null.");
        }
        // Set fields
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.params = params;
    }

    // Implementation of a Volley JsonArrayRequest
    @Override
    protected Request<JSONArray> doAPIRequest(Response.Listener<JSONArray> responseListener,
                                              Response.ErrorListener errorListener) {
        return new JsonArrayRequest(
                method,
                url,
                null,
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params != null) {
                    return params;
                } else {
                    return new HashMap<String, String>();
                }
            }
        };
    }

    @Override
    protected List<Map<String, String>> convertData(JSONArray data) {
        return convertJSONArrayToMap(data);
    }

    /**
     * Converts JSONArray to Map of strings
     *
     * @param array JSONArray to be converted
     * @return List<Map < String, String>>> that contains all JSONArray elements.
     * @throws IllegalStateException if JSONArray cannot be converted.
     */
    private static List<Map<String, String>> convertJSONArrayToMap(JSONArray array) {
        List<Map<String, String>> convertedData = new ArrayList<Map<String, String>>();
        for (int i = 0; i < array.length(); i++) {
            try {
                convertedData.add(new Gson().fromJson(array.getJSONObject(i).toString(),
                        HashMap.class));
            } catch (Exception e) {
                throw new IllegalStateException("Cannot convert data");
            }
        }
        return convertedData;
    }
}
