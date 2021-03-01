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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("test2", 5);
        }
        catch (Exception e) {}
        APIService.createExpenseIOU("10593bc886776acc3934716ebde2ea534510968a3574f622242e624d93014964", "3",jsonObject, this, new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        textView.setText(data.toString());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        textView.setText(errorMessage);
                    }
                });
    }


}
