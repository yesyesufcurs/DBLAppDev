package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class SelectMembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_members);
    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {

        // Redirect to the add/edit expense screen
        finish();
    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onCheckmark(View view) {

        // Redirect to the group screen
        finish();
    }
}