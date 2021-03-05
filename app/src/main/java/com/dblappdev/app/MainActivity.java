package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import TestAPICalls.TestAPILogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.helloWorld);
        JSONObject jsonObject = new JSONObject();
        Bitmap testImage = null;
        try{
            URL url = new URL("https://miro.medium.com/max/2400/1*1BUIofZgqVuR6nj8LbrRtQ.jpeg");
            testImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e){
            System.out.println(e);
        }
        APIService.createExpense("10593bc886776acc3934716ebde2ea534510968a3574f622242e624d93014964","testWithPic","10", testImage, "Description", "1", this, new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        textView.setText(data.toString());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
//                        textView.setText(error.toString());
                        textView.setText(errorMessage);
                    }
                });
    }


}
