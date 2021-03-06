package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Event handler for the Login button on the login screen
     * @param view The View instance of the button that was pressed
     */
    public void onLoginClick(View view) {

    }

    /**
     * Event handler for the Register button on the login screen
     * @param view The View instance of the button that was pressed
     */
    public void onRegisterClick(View view) {

        // Redirect to the Register screen
        // Start this as a new activity to make sure pressing the back button takes you back to this screen again
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}