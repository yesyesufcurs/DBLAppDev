package TestAPICalls;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dblappdev.app.api.RequestHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Only for test purposes. Only run to test, DO NOT USE in app implementation.
 */
public class TestAPILogin {
    public static void login(String username, String password, Context context, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("APIService.login.pre: username or password" +
                    "is null");
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://10.0.2.2:5000/login";
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

}
