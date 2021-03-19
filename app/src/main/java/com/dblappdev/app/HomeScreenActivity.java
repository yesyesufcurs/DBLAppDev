package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dblappdev.app.adapters.ExpenseGroupAdapter;
import com.dblappdev.app.dataClasses.LoggedInUser;

public class HomeScreenActivity extends AppCompatActivity {

    /**
     * This method gets invoked by Android upon the creation of a HomeScreenActivity
     * Firstly, this method should check whether the logged in instance in
     * {@link LoggedInUser} is not null.
     * When this happens, this method should obtain all the ExpenseGroups that the currently
     * logged in user is a part of.
     * Once these have been loaded, a recyclerview adapter for the ExpenseGroups should be
     * initiated with the retrieved ExpenseGroups as dataset.
     * @pre {@code {@link LoggedInUser#getInstance()} != null}
     * @throws RuntimeException if {@code {@link LoggedInUser#getInstance()} == null}
     */
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
     * This method gets called when the user presses the account button on the home screen.
     * When the user does so, a new EditProfile activity should be created and started.
     * This activity should not be closed, such that the user gets redirected to this screen
     * when they perform a backPress action in the newly created EditProfile activity.
     * Event handler for the account button
     * @param view The View instance of the button that was pressed
     */
    public void onAccount(View view) {
        // Redirect to the edit profile screen
        Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
        startActivity(editProfileIntent);
    }

    /**
     * This method gets called when the user presses the add button on the home screen.
     * When the user does so, a new AddJoinGroup activity should be created and started.
     * This activity should not be closed, such that the user gets redirected to this screen
     * when they perform a backPress action in the newly created AddJoinGroup activity.
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onAdd(View view) {
        // Redirect to add/join group screen
        Intent addJoinGroupScreenIntent = new Intent(this, AddJoinGroupActivity.class);
        startActivity(addJoinGroupScreenIntent);
    }

    /**
     * This method gets called when the user presses the logout button on the home screen.
     * When the user does so, this method should logout the user from the {@link LoggedInUser}
     * singleton class by calling {@code {@link LoggedInUser#logout()}}.
     * After this, a new Login activity should be created and started.
     * Lastly, this current activity should be finished, to prevent the user from being able to
     * go back without logging in.
     * Event handler for the logout button
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
     * This method gets called when the user presses an entry in the ExpenseGroup recyclerview.
     * When the user does so, this method should create and start a new GroupScreen activity,
     * linking the ExpenseGroup that belongs to the clicked entry.
     * After this, this activity should not be closed, such that the user gets redirected to this
     * screen when they go back from the GroupScreen activity.
     * Event handler for the group list items
     * @param view The View instance of the group entry that was pressed in the list
     */
    public void onItemClick(View view) {
        // Redirect to group screen
        Intent groupScreenIntent = new Intent(this, GroupScreenActivity.class);
        startActivity(groupScreenIntent);
    }
}