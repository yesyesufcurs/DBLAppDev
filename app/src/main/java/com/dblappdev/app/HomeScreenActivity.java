package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dblappdev.app.api.RecyclerViewAdapter;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Set the recyclerview and its settings
        RecyclerView recView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Event handler for the account button
     * @param view The View instance of the button that was pressed
     */
    public void onAccount(View view) {

        // Redirect to the edit profile screen
        Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
        startActivity(editProfileIntent);
    }

    /**
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onAdd(View view) {
        // Redirect to add/join group screen
        Intent addJoinGroupScreenIntent = new Intent(this, AddJoinGroupActivity.class);
        startActivity(addJoinGroupScreenIntent);
    }

    /**
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onLogout(View view) {

        // Redirect to the login screen
        Intent loginScreenIntent = new Intent(this, LoginActivity.class);
        startActivity(loginScreenIntent);
        // Make sure the user can't go back to the home screen by finishing this activity
        finish();
    }

    /**
     * TODO: Temp function for navigation towards the group screen
     * Should eventually be replaced by a list of groups the user is part of, each of which redirects to its unique group screen
     * @param view
     */
    public void onTempClick(View view) {

        // Redirect to group screen
        Intent groupScreenIntent = new Intent(this, GroupScreenActivity.class);
        startActivity(groupScreenIntent);
    }
}