package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
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

    void showExpenses(List<Map<String, String>> data, TextView textView){
        textView.setText(data.toString());
    }

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
            url = new URL("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/ReceiptSwiss.jpg/800px-ReceiptSwiss.jpg");
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        APIService.getExpenseGroupExpenses("0949d532d55b0cb6da0ee09753d6900dce0eb3926bb127c2f3d6b9f1c1db7d5b",
                "42957", this, new APIResponse<List<Map<String, String>>>() {

            @Override
            public void onResponse(List<Map<String, String>> data) {
                showExpenses(data, textView);
            }

            @Override
            public void onErrorResponse(VolleyError error, String errorMessage) {

            }
        });






//        APIService.detectText("0949d532d55b0cb6da0ee09753d6900dce0eb3926bb127c2f3d6b9f1c1db7d5b", bmp, this, new APIResponse<String>() {
//
//            @Override
//            public void onResponse(String data) {
//                textView.setText(data);
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error, String errorMessage) {
//                textView.setText(errorMessage);
//            }
//        });


//        APIService.createExpense("0949d532d55b0cb6da0ee09753d6900dce0eb3926bb127c2f3d6b9f1c1db7d5b",
//                null,"Title","10", bmp,"1","42957",
//                this, new APIResponse<String>(){
//            @Override
//            public void onResponse(String data) {
//                textView.setText("Expense ID: " + data);
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error, String errorMessage) {
//                textView.setText(errorMessage);
//            }
//        });

//        APIService.getExpensePicture("832062e78d25084853dd00edd1e9bc430b810a163482431da7446941fe893fd3",
//                "3",this, new APIResponse<Bitmap>() {
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

//        APIService.createExpense("832062e78d25084853dd00edd1e9bc430b810a163482431da7446941fe893fd3", "physicalTest", "NewExpense","10",null,"b","42957",this, new APIResponse<String>(){
//
//            @Override
//            public void onResponse(String data) {
//                textView.setText(data);
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error, String errorMessage) {
//                textView.setText(errorMessage);
//            }
//        });
    }


}
