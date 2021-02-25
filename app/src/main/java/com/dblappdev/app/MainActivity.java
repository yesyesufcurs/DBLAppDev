package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;

import org.json.JSONArray;
import org.json.JSONObject;

import TestAPICalls.TestAPILogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.helloWorld);
        APIService.register("appTest", "test123", "apptest@test.nl",
                this, new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        textView.setText(data);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        textView.setText(errorMessage);
                    }
                });
    }


}
