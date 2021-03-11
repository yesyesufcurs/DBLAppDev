package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GroupScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_screen);
    }

    /**
     * Event handler for the settings button
     * @param view The View instance of the button that was pressed
     */
    public void onSettings(View view) {

        // Redirect to the home screen
        Intent groupSettingsIntent = new Intent(this, GroupSettingsActivity.class);
        startActivity(groupSettingsIntent);
    }

    /**
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onAdd(View view) {

        // Redirect to the home screen
        Intent expenseDetailsIntent = new Intent(this, ExpenseDetailsActivity.class);
        startActivity(expenseDetailsIntent);
    }

    /**
     * Event handler for the search button
     * @param view The View instance of the button that was pressed
     */
    public void onSearch(View view) {

    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {

        // Redirect to the home screen
        finish();
    }
}