package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ExpenseDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);
    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {

        // Redirect to the group screen
        finish();
    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onCheckmark(View view) {

        // Redirect to the select members screen
        Intent selectMembersIntent = new Intent(this, SelectMembersActivity.class);
        startActivity(selectMembersIntent);
    }
}