package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import TestAPICalls.TestAPILogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.helloWorld);
        ImageView im1 = (ImageView) findViewById(R.id.imageView);
        JSONObject jsonObject = new JSONObject();
        // Convert link with website to bitmap object
        URL url = null;
        Bitmap bmp = null;
        try {
            url = new URL("https://i.ibb.co/5chxVNr/IMG-20210309-211018.jpg");
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
        APIService.createExpenseGroup("832062e78d25084853dd00edd1e9bc430b810a163482431da7446941fe893fd3","newTestGroup", this, new APIResponse<String>(){
            @Override
            public void onResponse(String data) {
                textView.setText(data);
            }

            @Override
            public void onErrorResponse(VolleyError error, String errorMessage) {
                textView.setText(errorMessage);
            }


        });
//        APIService.getExpensePicture("aea35fd516cb721c5a32451d9cf78764533f64fc3ab2c92305be461fbe4c183b","4",this, new APIResponse<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap data) {
////                        textView.setText(data.toString());
//                        im1.setImageBitmap(data);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error, String errorMessage) {
//                        textView.setText(error.toString());
////                        throw error;
////                        textView.setText(errorMessage);
//                    }
//                });
    }


}
