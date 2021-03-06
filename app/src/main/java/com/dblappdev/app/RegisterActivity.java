package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * Event handler for the Register button on the register screen
     * @param view The View instance of the button that was pressed
     */
    public void onRegisterClick(View view) {

        // Redirect to the Create profile screen
        Intent createProfileIntent = new Intent(this, CreateProfileActivity.class);
        startActivity(createProfileIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // leave this method empty, to prevent the user from leaving the screen
    }
}