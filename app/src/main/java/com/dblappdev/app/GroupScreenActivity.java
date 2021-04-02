package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dblappdev.app.adapters.ExpenseAdapter;
import com.dblappdev.app.adapters.ExpenseGroupAdapter;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;
import com.dblappdev.app.dataClasses.Expense;
import com.dblappdev.app.dataClasses.ExpenseGroup;
import com.dblappdev.app.dataClasses.LoggedInUser;
import com.dblappdev.app.dataClasses.User;
import com.dblappdev.app.gregservice.GregService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupScreenActivity extends AppCompatActivity {

    // Semaphore to prevent multiple requests from happening at the same time, potentially
    // interfering with each other
    boolean isRequestHappening = false;
    int expenseGroupID;
    public int expenseID;

    private RecyclerView recView;
    private ExpenseAdapter adapter;


    // Instance variable of GroupScreenActivity
    public static GroupScreenActivity instance;


    // List containing the expenses to be shown
    private ArrayList<Expense> expenses = new ArrayList<>();

    /**
     * This method gets invoked by Android upon the creation of a GroupScreenActivity
     * Firstly, this method should check whether the logged in instance in
     * {@link LoggedInUser} is not null.
     * If this is null, throw a RuntimeException stating that something went wrong with logging in.
     * Secondly, this method should check which expense group ID is linked. If the ID is null,
     * throw a RuntimeException stating that something went wrong with displaying the expense group.
     * Otherwise, this method should obtain all the Expenses that are in the group the user is
     * currently looking at.
     * Once these have been loaded, a recyclerview adapter for the Expenses should be
     * initiated with the retrieved Expenses as dataset.
     * @pre {@code {@link LoggedInUser#getInstance()} != null}
     * @throws RuntimeException if {@code {@link LoggedInUser#getInstance()} == null}
     * @throws RuntimeException if no expense group ID is linked
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard code generated by Android Studio
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_screen);

        // Check if the Singleton class LoggedInUser is initialized
        if (LoggedInUser.getInstance() == null) {
            throw new RuntimeException("Something went wrong with logging in: no loggged in user" +
                    " found upon creation of the home screen!");
        }

        // Get the linked expense group ID and check if it was properly defined
        Bundle bundle = getIntent().getExtras();
        if (!getIntent().hasExtra("EXPENSE_GROUP_ID")) {
            throw new RuntimeException("Something went wrong with opening the expense group: no " +
                    "expense group selected.");
        }
        // Set instance variable
        instance = this;
        expenseGroupID = bundle.getInt("EXPENSE_GROUP_ID");
        // Set the group name in the UI
        // If this page is reached via the AddJoinGroupActivity, the name is not linked
        if (!getIntent().hasExtra("EXPENSE_GROUP_NAME")) {
            isRequestHappening = true;
            getExpenseGroupName(this);
        } else {
            String name = bundle.getString("EXPENSE_GROUP_NAME");
            ((TextView) findViewById(R.id.usernameText)).setText(name);
        }

        // Get all the expense groups the logged in user is part of
        if (!isRequestHappening) {
            // Update semaphore
            isRequestHappening = true;
            // This method will also deal with the instantiating of the recycler view
            getExpenses(this);
        }
    }

    /**
     * This method gets called when the user presses the settings button on the home screen.
     * When the user does so, a new GroupSettings activity should be created and started.
     * The current activity should not be closed, such that the user gets redirected to this screen
     * when they perform a backPress action in the newly created GroupSettings activity.
     * Event handler for the settings button
     * @param view The View instance of the button that was pressed
     */
    public void onSettings(View view) {
        // Redirect to the settings screen
        Intent groupSettingsIntent = new Intent(this, GroupSettingsActivity.class);
        // Link the ExpenseGroup by adding the group ID as extra on the intent
        groupSettingsIntent.putExtra("EXPENSE_GROUP_ID", expenseGroupID);
        startActivityForResult(groupSettingsIntent, 0);
    }

    /**
     * This method gets called when the user presses the add button on the group screen.
     * When the user does so, a new AddExpense activity should be created and started.
     * The current activity should not be closed, such that the user gets redirected to this screen
     * when they perform a backPress action in the newly created AddExpense activity.
     * Event handler for the add button
     * @param view The View instance of the button that was pressed
     */
    public void onAdd(View view) {
        // Redirect to the expense details screen
        Intent expenseDetailsIntent = new Intent(this, ExpenseDetailsActivity.class);
        // Link the ExpenseGroup by adding the group ID as extra on the intent
        expenseDetailsIntent.putExtra("EXPENSE_GROUP_ID", expenseGroupID);
        // Link the mode
        expenseDetailsIntent.putExtra("MODE", "ADD");
        startActivity(expenseDetailsIntent);
    }

    /**
     * This method gets called when the user presses an entry in the Expenses recyclerview.
     * When the user does so, this method should create and start a new ExpenseDetails activity,
     * linking the Expense that belongs to the clicked expense.
     * After this, this activity should not be closed, such that the user gets redirected to this
     * screen when they go back from the ExpenseDetails activity.
     * Event handler for the expense list items
     * @param view The View instance of the expense entry that was pressed in the list
     */
    public void onItemClick(View view) {

        // Redirect to the expense details screen
        Intent expenseDetailsIntent = new Intent(this, ExpenseDetailsActivity.class);
        // Link the ExpenseGroup by adding the group ID as extra on the intent
        expenseDetailsIntent.putExtra("EXPENSE_GROUP_ID", expenseGroupID);
        // Link the mode
        expenseDetailsIntent.putExtra("MODE", "EDIT");
        // Link the expense ID
        expenseDetailsIntent.putExtra("EXPENSE_ID", (Integer) view.getTag());
        expenseID = (Integer) view.getTag();
        startActivity(expenseDetailsIntent);
    }

    public void onRemove(View view) {
        // remove expense
        String expenseID = view.getTag().toString();
        if (!isRequestHappening) {
            isRequestHappening = true;

            removeExpense(expenseID, this);
        }
    }

    /**
     * Event handler for the search button
     * @param view The View instance of the button that was pressed
     */
    public void onSearch(View view) {

    }

    /**
     * This method gets called when the user performs a backPress action.
     * When the user does so, the user will be redirected to the already existing
     * HomeScreenActivity.
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {
        // Close old HomeScreenActivity
        HomeScreenActivity.instance.finish();
        // Open new HomeScreenActivity
        Intent homeScreenIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(homeScreenIntent);
        // Redirect to the home screen
        finish();
    }
    /**
     * This method creates a getExpenseGroups API call to retrieve all the expenses the currently
     * logged in user is part of.
     * Upon success, it parses the data into an ArrayList of ExpenseGroups and instantiates
     * a recycler view with the retrieved data.
     * Upon failure, it shows a Toast with the error message.
     * @param context Context in which the API request and RecyclerView instantiating happens
     */
    private void getExpenses(Context context) {
        APIService.getExpenseGroupExpenses(LoggedInUser.getInstance().getApiKey(),
                Integer.toString(expenseGroupID), context,
                new APIResponse<List<Map<String, String>>>() {
                    @Override
                    public void onResponse(List<Map<String, String>> data) {
                        // Parse the data into the expenses ArrayList
                        for (Map<String, String> group : data) {
                            int id = Integer.parseInt(group.get("id"));
                            String title = group.get("title");
                            float amount = Float.parseFloat(group.get("amount"));
                            String content = group.get("content");
                            int expense_group_id = Integer.parseInt(group.get("expense_group_id"));
                            User user = new User(group.get("user_id"));
                            // TODO: Fix timestamp property of Expense class
                            // int timestamp = Integer.parseInt(group.get("timestamp"));
                            Expense expense = new Expense(id, expense_group_id, user, amount, title,
                                    content, null);
                            expenses.add(expense);
                        }
                        // Set the recyclerview and its settings
                        recView = (RecyclerView) findViewById(R.id.recyclerViewExpense);
                        View.OnClickListener clickListener = view -> onItemClick(view);
                        View.OnClickListener removeListener = view -> onRemove(view);
                        adapter = new ExpenseAdapter(clickListener, removeListener, expenses);
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

    private void getExpenseGroupName(Context context) {
        APIService.getExpenseGroup(LoggedInUser.getInstance().getApiKey(), Integer.toString(expenseGroupID),
                context,
                new APIResponse<List<Map<String, String>>>() {
                    @Override
                    public void onResponse(List<Map<String, String>> data) {
                        String name = data.get(0).get("name");
                        ((TextView) findViewById(R.id.usernameText)).setText(name);
                        isRequestHappening = false;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        GregService.showErrorToast(errorMessage, context);
                        isRequestHappening = false;
                    }
                });
    }

    private void removeExpense(String expenseID, Context context) {
        APIService.removeExpense(LoggedInUser.getInstance().getApiKey(), expenseID, context,
                new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        GregService.showErrorToast("Successfully removed expense!", context);
                        for (Iterator<Expense> iterator = expenses.iterator(); iterator.hasNext();) {
                            Expense exp = iterator.next();
                            if (Integer.toString(exp.getId()).equals(expenseID)) {
                                iterator.remove();
                                adapter.notifyDataSetChanged();
                            }
                        }
                        isRequestHappening = false;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        GregService.showErrorToast(errorMessage, context);
                        isRequestHappening = false;
                    }
                });
    }
}
