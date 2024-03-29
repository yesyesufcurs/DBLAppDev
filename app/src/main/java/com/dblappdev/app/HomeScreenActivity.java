package com.dblappdev.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.dblappdev.app.adapters.ExpenseGroupAdapter;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.ExpenseGroupService;
import com.dblappdev.app.dataClasses.ExpenseGroup;
import com.dblappdev.app.dataClasses.LoggedInUser;
import com.dblappdev.app.dataClasses.User;
import com.dblappdev.app.gregservice.GregService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeScreenActivity extends AppCompatActivity {

    // Semaphore to prevent multiple requests from happening at the same time, potentially
    // interfering with each other
    boolean isRequestHappening = false;

    //  Publicly accessible variable for this HomeScreenActivity.
    public static HomeScreenActivity instance;
    // List containing the expense groups to be shown
    private ArrayList<ExpenseGroup> expenseGroups = new ArrayList<>();

    /**
     * This method gets invoked by Android upon the creation of a HomeScreenActivity
     * Firstly, this method should check whether the logged in instance in
     * {@link LoggedInUser} is not null.
     * If this is null, throw a RuntimeException stating that something went wrong with logging in.
     * Otherwise, this method should obtain all the ExpenseGroups that the currently
     * logged in user is a part of.
     * Once these have been loaded, a recyclerview adapter for the ExpenseGroups should be
     * initiated with the retrieved ExpenseGroups as dataset.
     * @pre {@code {@link LoggedInUser#getInstance()} != null}
     * @throws RuntimeException if {@code {@link LoggedInUser#getInstance()} == null}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard code generated by Android Studio
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Check if the Singleton class LoggedInUser is initialized
        if (LoggedInUser.getInstance() == null) {
            throw new RuntimeException(
                    "Something went wrong with logging in: no logged in user" +
                    " found upon creation of the home screen!");
        }

        // Set instance variable
        instance = this;

        // Get all the expense groups the logged in user is part of
        if (!isRequestHappening) {
            // Update semaphore
            isRequestHappening = true;
            // This method will also deal with the instantiating of the recycler view
            getExpenseGroups(this);
        }

    }

    /**
     * This method gets called when the user presses the add button on the home screen.
     * When the user does so, a new AddJoinGroup activity should be created and started.
     * The current activity should not be closed, such that the user gets redirected to this screen
     * when they perform a backPress action in the newly created AddJoinGroup activity.
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onAdd(View view) {
        // Redirect to add/join group screen
        Intent addJoinGroupScreenIntent = new Intent(
                this,
                AddJoinGroupActivity.class);
        startActivity(addJoinGroupScreenIntent);
    }

    /**
     * This method gets called when the user presses the logout button on the home screen.
     * When the user does so, this method should logout the user from the {@link LoggedInUser}
     * singleton class by calling {@code {@link LoggedInUser#logOut()}}.
     * After this, a new Login activity should be created and started.
     * Lastly, this current activity should be finished, to prevent the user from being able to
     * go back without logging in.
     * Event handler for the logout button
     * @param view The View instance of the button that was pressed
     */
    public void onLogout(View view) {
        // Clear the current instance of the LoggedInUser singleton
        LoggedInUser.logOut();
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
        // Link the ExpenseGroup by adding the group ID as extra on the intent
        groupScreenIntent.putExtra("EXPENSE_GROUP_ID", (Integer) view.getTag());
        String name = ((TextView) view.findViewById(R.id.item_name)).getText().toString();
        // Link the ExpenseGroup name
        groupScreenIntent.putExtra("EXPENSE_GROUP_NAME", name);
        startActivity(groupScreenIntent);
    }

    @Override
    // logout on back pressed in home screen
    public void onBackPressed() {
        onLogout(null);
    }

    /**
     * This method creates a getExpenseGroups API call to retrieve all the expenses the currently
     * logged in user is part of.
     * Upon success, it parses the data into an ArrayList of ExpenseGroups and instantiates
     * a recycler view with the retrieved data.
     * Upon failure, it shows a Toast with the error message.
     * @param context Context in which the API request and RecyclerView instantiating happens
     */
    private void getExpenseGroups(Context context) {
        ExpenseGroupService.getExpenseGroups(LoggedInUser.getInstance().getApiKey(), context,
                new APIResponse<List<Map<String, String>>>() {
                    @Override
                    public void onResponse(List<Map<String, String>> data) {
                        // Parse the data into the ExpenseGroup ArrayList
                        for (Map<String, String> group : data) {
                            int id = Integer.parseInt(group.get("id"));
                            String title = group.get("name");
                            User moderator = new User(group.get("moderator_id"));
                            ExpenseGroup expenseGroup = new ExpenseGroup(id, title, moderator);
                            expenseGroups.add(expenseGroup);
                        }
                        // Set the recyclerview and its settings
                        //todo explain variables
                        RecyclerView recView =
                                (RecyclerView) findViewById(R.id.recyclerViewExpenseGroup);
                        View.OnClickListener listener = view -> onItemClick(view);
                        ExpenseGroupAdapter adapter = new ExpenseGroupAdapter(
                                listener,
                                expenseGroups);
                        recView.setAdapter(adapter);
                        recView.setLayoutManager(new LinearLayoutManager(context));
                        // Update semaphore
                        isRequestHappening = false;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        // Show error and update semaphore
                        GregService.showErrorToast(errorMessage, context);
                        isRequestHappening = false;
                    }
                });
    }

}