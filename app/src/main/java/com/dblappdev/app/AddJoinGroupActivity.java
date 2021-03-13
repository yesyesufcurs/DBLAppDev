package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AddJoinGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_join_group);
    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {
        finish();
    }

    /**
     * Event handler for the join group button
     * @param view The View instance of the button that was pressed
     */
    public void onJoinGroup(View view) {
        // Redirect to group screen
        Intent groupScreenIntent = new Intent(this, GroupScreenActivity.class);
        startActivity(groupScreenIntent);
        finish();
    }

    /**
     * Event handler for the create group button
     * @param view The View instance of the button that was pressed
     */
    public void onCreateGroup(View view) {
        // Redirect to group screen
        Intent groupScreenIntent = new Intent(this, GroupScreenActivity.class);
        startActivity(groupScreenIntent);
        finish();
    }
}