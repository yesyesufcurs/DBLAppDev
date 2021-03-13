package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dblappdev.app.api.RecyclerViewAdapter;

public class GroupScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_screen);

        // Set the recyclerview and its settings
        RecyclerView recView = (RecyclerView) findViewById(R.id.recyclerView);
        View.OnClickListener listener = view -> onItemClick(view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(listener);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Event handler for the settings button
     * @param view The View instance of the button that was pressed
     */
    public void onSettings(View view) {

        // Redirect to the settings screen
        Intent groupSettingsIntent = new Intent(this, GroupSettingsActivity.class);
        startActivity(groupSettingsIntent);
    }

    /**
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onAdd(View view) {

        // Redirect to the expense details screen
        Intent expenseDetailsIntent = new Intent(this, ExpenseDetailsActivity.class);
        startActivity(expenseDetailsIntent);
    }

    /**
     * Event handler for the expense list items
     * @param view The View instance of the expense entry that was pressed in the list
     */
    public void onItemClick(View view) {

        // Redirect to the expense details screen
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