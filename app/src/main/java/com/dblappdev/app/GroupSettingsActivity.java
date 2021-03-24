package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dblappdev.app.adapters.MemberBalanceAdapter;

public class GroupSettingsActivity extends AppCompatActivity {
    int expenseGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        // Get the linked expense group ID and check if it was properly defined
        Bundle bundle = getIntent().getExtras();
        if (!getIntent().hasExtra("EXPENSE_GROUP_ID")) {
            throw new RuntimeException("runtime exc");
        }
        expenseGroupID = bundle.getInt("EXPENSE_GROUP_ID");

        ((TextView) findViewById(R.id.groupNameText)).setText(Integer.toString(expenseGroupID));

        // Set the recyclerview and its settings
        RecyclerView recView = (RecyclerView) findViewById(R.id.recyclerViewMembersBalance);
        View.OnClickListener listener = view -> onRemove(view);
        MemberBalanceAdapter adapter = new MemberBalanceAdapter(listener);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(this));
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
     * Event handler for the leave button
     * @param view The View instance of the button that was pressed
     */
    public void onLeave(View view) {

    }

    /**
     * Event handler for
     * @param view
     */
    public void onRemove(View view) {

    }
}