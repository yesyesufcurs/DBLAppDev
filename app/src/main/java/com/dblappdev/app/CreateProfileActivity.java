package com.dblappdev.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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

        // Redirect to the home screen
        Intent homeScreenIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(homeScreenIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // leave this method empty, to prevent the user from leaving the screen
    }
}