package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CreateProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
    }

    /**
     * Event handler for the checkmark button
     * @param view The View instance of the button that was pressed
     */
    public void onCompleteProfile(View view) {

    }
}