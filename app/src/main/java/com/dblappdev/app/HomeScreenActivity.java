package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    /**
     * Event handler for the account button
     * @param view The View instance of the button that was pressed
     */
    public void onAccount(View view) {

    }

    /**
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onAdd(View view) {

    }

    /**
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onLogout(View view) {

        // Redirect to the login screen
        Intent loginScreenIntent = new Intent(this, LoginActivity.class);
        startActivity(loginScreenIntent);
        // Make sure the user can't go back to the home screen by finish this activity
        finish();
    }
}