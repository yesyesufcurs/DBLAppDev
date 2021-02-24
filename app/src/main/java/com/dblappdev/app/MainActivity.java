package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import TestAPICalls.TestAPILogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.helloWorld);
        TestAPILogin.login("", "test123", this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String responsebody;
                try {
                    responsebody = new String(error.networkResponse.data, "utf-8");
                    textView.setText(new JSONObject(responsebody).optString("text"));
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
            }
        });
    }


}
