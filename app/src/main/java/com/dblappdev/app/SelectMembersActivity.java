package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dblappdev.app.api.RecyclerViewAdapter;

public class SelectMembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_members);

        // Set the recyclerview and its settings
        RecyclerView recView = (RecyclerView) findViewById(R.id.recyclerViewMembers);
        View.OnClickListener listener = view -> onItemClick(view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(listener);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {

        // Redirect to the add/edit expense screen
        Intent expenseDetailsIntent = new Intent(this, ExpenseDetailsActivity.class);
        startActivity(expenseDetailsIntent);
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

    /**
     * Event handler for the member list items
     * @param view The View instance of the group entry that was pressed in the list
     */
    public void onItemClick(View view) {
        // do nothing for this recyclerview
    }
}