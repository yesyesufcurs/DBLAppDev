package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dblappdev.app.adapters.ExpenseGroupAdapter;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Set the recyclerview and its settings
        RecyclerView recView = (RecyclerView) findViewById(R.id.recyclerViewExpenseGroup);
        View.OnClickListener listener = view -> onItemClick(view);
        ExpenseGroupAdapter adapter = new ExpenseGroupAdapter(listener);
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
     * Event handler for the group list items
     * @param view The View instance of the group entry that was pressed in the list
     */
    public void onItemClick(View view) {
        // Redirect to group screen
        Intent groupScreenIntent = new Intent(this, GroupScreenActivity.class);
        startActivity(groupScreenIntent);
    }
}