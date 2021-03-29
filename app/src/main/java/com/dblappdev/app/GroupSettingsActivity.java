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
import com.dblappdev.app.adapters.MemberBalanceAdapter;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;
import com.dblappdev.app.dataClasses.Expense;
import com.dblappdev.app.dataClasses.ExpenseGroup;
import com.dblappdev.app.dataClasses.LoggedInUser;
import com.dblappdev.app.dataClasses.User;
import com.dblappdev.app.gregservice.GregService;

import java.util.List;
import java.util.Map;

public class GroupSettingsActivity extends AppCompatActivity {

    boolean isRequestHappening = false;
    ExpenseGroup expenseGroup;
    // Some actions on this screen are only allowed to be performed by a moderator
    User moderator;

    /**
     * This method gets invoked by Android upon the creation of a GroupScreenActivity
     * Firstly, this method should check whether the logged in instance in
     * {@link LoggedInUser} is not null.
     * If this is null, throw a RuntimeException stating that something went wrong with logging in.
     * Secondly, this method should check which expense group ID is linked. If the ID is null,
     * throw a RuntimeException stating that something went wrong with displaying the expense group
     * settings.
     * Otherwise, this method should obtain all the Users that are in the group and the amount
     * of money owed by the user to each of them.
     * Once these have been loaded, a recyclerview adapter for the users should be
     * initiated with the retrieved Users as dataset.
     * @pre {@code {@link LoggedInUser#getInstance()} != null}
     * @throws RuntimeException if {@code {@link LoggedInUser#getInstance()} == null}
     * @throws RuntimeException if no expense group ID is linked
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        // Get the linked expense group ID and check if it was properly defined
        Bundle bundle = getIntent().getExtras();
        if (!getIntent().hasExtra("EXPENSE_GROUP_ID")) {
            throw new RuntimeException("Expense Group Id unknown");
        }
        int expenseGroupID = bundle.getInt("EXPENSE_GROUP_ID");

        ((TextView) findViewById(R.id.groupNameText)).setText(Integer.toString(expenseGroupID));

        if (!isRequestHappening) {
            // Update semaphore
            isRequestHappening = true;
            // This method will also deal with the instantiating of the recycler view
            getExpenseGroup(this,  expenseGroupID);
        }
    }

    /**
     * This method gets called when the user presses the Back button.
     * When this happens, this activity should be finished without saving any changes.
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {
        // Redirect to the group screen
        finish();
    }

    /**
     * This method gets called when the user presses the leave button
     * When this happens, this method should make a request to remove the currently logged in user
     * from the expense group that is linked to this activity.
     * If this request gives an error, a Toast with the error messages should be displayed.
     * If this request is successful, this activity should be finished, as well as the
     * GroupScreenActivity that is still opened.
     * Event handler for the leave button
     * @param view The View instance of the button that was pressed
     */
    public void onLeave(View view) {
        int expenseGroupID = getIntent().getExtras().getInt("EXPENSE_GROUP_ID");
        isRequestHappening = true;
        leaveGroup(expenseGroupID, this);
    }

    /**
     * This method gets called when the user presses the remove button in an item in the member list.
     * When this happens, this method should make a request to remove the user that is linked to
     * the clicked item in the member list from the expense group that is linked to this activity.
     * If this request gives an error, a Toast with the error message should be displayed.
     * If this request is successful, a Toast message saying the user has been removed should be
     * displayed.
     * TODO: Possibly, the RecyclerView can be updated, but this is not needed for the initial implementation.
     * Event handler for removing a user
     * @param view
     */
    public void onRemove(View view) {
        User loggedInUser = LoggedInUser.getInstance().getUser();
        if (loggedInUser.getUsername().equals(moderator.getUsername())) {
            // User is the moderator and can thus make the remove request
            String username = view.getTag().toString();
            int expenseGroupID = getIntent().getExtras().getInt("EXPENSE_GROUP_ID");
            isRequestHappening = true;
            removeFromGroup(username, expenseGroupID, this);
        } else {
            GregService.showErrorToast("Only a moderator can perform this action!", this);
        }
    }

    /**
     * This method gets called when the user presses the remove group button on the end of the page.
     * When this happens, this method should make a request to remove the expense group that is
     * linked to this activity.
     * If this request gives an error, a Toast with the error message should be displayed.
     * If this request is successful, this activity should be finish, as well as the
     * GroupScreenActivity that is still opened.
     * Event handler for removing the group
     * @param view
     */
    public void onRemoveGroup(View view) {
        User loggedInUser = LoggedInUser.getInstance().getUser();
        if (loggedInUser.getUsername().equals(moderator.getUsername())) {
            // User is the moderator and can thus make the remove group request
            isRequestHappening = true;
            int expenseGroupID = getIntent().getExtras().getInt("EXPENSE_GROUP_ID");
            removeGroup(expenseGroupID, this);
        } else {
            GregService.showErrorToast("Only a moderator can perform this action!", this);
        }
    }

    /**
     * This method creates a getExpenseGroupMembers API call to retrieve information about the
     * users in the expense group
     * Upon success, it parses the data and adds all the users to the expenseGroup as User
     * instances. It then sets the balance of all those users to 0 and calls getBalance to
     * correct this value.
     * Upon failure, it shows a Toast with the error message.
     * @param context Context in which the API request and RecyclerView instantiating happens
     * @param expenseGroupID id of the group this screen was opened for
     */
    private void getUsers(Context context, int expenseGroupID) {
        APIService.getExpenseGroupMembers(LoggedInUser.getInstance().getApiKey(),
                Integer.toString(expenseGroupID), context,
                new APIResponse<List<Map<String, String>>>() {
                    @Override
                    public void onResponse(List<Map<String, String>> data) {
                        // Parse the data into the expenses ArrayList
                        for (Map<String, String> group : data) {
                            String id = group.get("user_id");
                            if (!id.equals(LoggedInUser.getInstance().getUser().getUsername())) {
                                User user = new User(id);
                                expenseGroup.addUser(user);
                                expenseGroup.setSingleBalance(user, 0.0f);
                            }
                        }
                        getBalance(context, expenseGroupID);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        // Show error and update semaphore
                        GregService.showErrorToast(errorMessage + "in b", context);
                        isRequestHappening = false;
                    }
                });
    }

    /**
     * This method creates a getUserOwedTotal API call to retrieve information about the
     * users in the expense group
     * Upon success, it parses the data and sets the balance of all users to the correct amount
     * Upon failure, it shows a Toast with the error message.
     * @param context Context in which the API request and RecyclerView instantiating happens
     * @param expenseGroupID id of the group this screen was opened for
     */
    private void getBalance(Context context, int expenseGroupID) {
        APIService.getUserOwedTotal(LoggedInUser.getInstance().getApiKey(),
                Integer.toString(expenseGroupID), context,
                new APIResponse<List<Map<String, String>>>() {
                    @Override
                    public void onResponse(List<Map<String, String>> data) {
                        // Parse the data into the expenses ArrayList
                        for (Map<String, String> group : data) {
                            String id = group.get("user_id");
                            float amount = Float.parseFloat(group.get("amount"));
                            for (User user : expenseGroup.getUsers()) {
                                if (user.getUsername().equals(id)) {
                                    expenseGroup.setSingleBalance(user, amount);
                                    break;
                                }
                            }
                        }
                        // Set the recyclerview and its settings
                        RecyclerView recView = (RecyclerView) findViewById(R.id.recyclerViewMembersBalance);
                        View.OnClickListener listener = view -> onRemove(view);
                        MemberBalanceAdapter adapter = new MemberBalanceAdapter(listener,
                                expenseGroup.getUsers(), expenseGroup.getBalance());
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

    /**
     * This method creates a getExpenseGroup API call to retrieve information about the current
     * expense group.
     * Upon success, it parses the data into an ExpenseGroup instance and calls getUsers to collect
     * more data about the group.
     * Upon failure, it shows a Toast with the error message.
     * @param context Context in which the API request and RecyclerView instantiating happens
     * @param expenseGroupID id of the group this screen was opened for
     */
    private void getExpenseGroup(Context context, int expenseGroupID) {
        APIService.getExpenseGroup(LoggedInUser.getInstance().getApiKey(),
                Integer.toString(expenseGroupID),
                context,
                new APIResponse<List<Map<String, String>>>() {
                    @Override
                    public void onResponse(List<Map<String, String>> data) {
                        String name =data.get(0).get("name");
                        String mod = data.get(0).get("moderator_id");
                        // Set the moderator for later purposes
                        moderator = new User(mod);
                        expenseGroup = new ExpenseGroup(expenseGroupID, name, new User(mod));
                        // Set the recyclerview and its settings
                        getUsers(context, expenseGroupID);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        // Show error and update semaphore
                        GregService.showErrorToast(errorMessage, context);
                        isRequestHappening = false;
                    }
                });
    }

    private void removeGroup(int expenseGroupID, Context context) {
        APIService.removeExpenseGroup(LoggedInUser.getInstance().getApiKey(),
                Integer.toString(expenseGroupID), context
                , new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        // Clsoe the group screen activity, since the group is removed
                        GroupScreenActivity.instance.finish();
                        // Update the homepage to portray the deletion of the group
                        refreshHomepage();
                        finish();
                        isRequestHappening = false;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        GregService.showErrorToast(errorMessage, context);
                        isRequestHappening = false;
                    }
                });
    }

    private void removeFromGroup(String username, int expenseGroupID, Context context) {
        APIService.removeFromExpenseGroup(LoggedInUser.getInstance().getApiKey(),
                username, Integer.toString(expenseGroupID), context,
                new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        GregService.showErrorToast("User " + username + " has been " +
                                "removed from the group!", context);
                        isRequestHappening = false;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        GregService.showErrorToast(errorMessage, context);
                        isRequestHappening = false;
                    }
                });
    }

    private void leaveGroup(int expenseGroupID, Context context) {
        APIService.removeFromExpenseGroup(LoggedInUser.getInstance().getApiKey(),
                LoggedInUser.getInstance().getUser().getUsername(), Integer.toString(expenseGroupID),
                context,
                new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        // Close the group screen activity, since the group has been left
                        GroupScreenActivity.instance.finish();
                        // Update the home page to portray the leaving of the group
                        refreshHomepage();
                        finish();
                        isRequestHappening = false;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        GregService.showErrorToast(errorMessage, context);
                        isRequestHappening = false;
                    }
                });
    }

    private void refreshHomepage() {
        HomeScreenActivity.instance.finish();
        // Open new HomeScreenActivity
        Intent homeScreenIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(homeScreenIntent);
    }
}