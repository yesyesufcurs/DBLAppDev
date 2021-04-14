package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.ExpenseGroupService;
import com.dblappdev.app.dataClasses.LoggedInUser;
import com.dblappdev.app.gregservice.GregService;

public class AddJoinGroupActivity extends AppCompatActivity {

    // Semaphore to prevent multiple requests from interfering with each other
    boolean isRequestHappening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_join_group);
    }

    /**
     * This method gets called when the user performs a backPress action.
     * When the user does so, the user will be redirected to the already existing
     * HomeScreenActivity.
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {
        finish();
    }

    /**
     * This method should try and join the group whose ID is equal to the value in
     * join_group_input_text.
     * This means that this method should perform a request to join the group with the given ID.
     * If the request returns an error,
     * this method should show a Toast displaying the error message.
     * If the request is successful, this method should open a new GroupScreen activity of the newly
     * joined group and should finish this activity.
     * Since this method uses the LoggedInUser to check which user to add to the expense, this
     * method should check whether the singleton instance is defined, and throw an exception
     * otherwise.
     * @pre {@code {@link LoggedInUser#getInstance()} != null}
     * @throws RuntimeException if {@code {@link LoggedInUser#getInstance()} == null}
     * Event handler for the join group button
     * @param view The View instance of the button that was pressed
     */
    public void onJoinGroup(View view) {
        //todo explain variables
        EditText givenIDET = findViewById(R.id.join_group_input_text);
        int givenID = Integer.parseInt(givenIDET.getText().toString());

        // Check if the Singleton class LoggedInUser is initialized
        if (LoggedInUser.getInstance() == null) {
            throw new RuntimeException("Something went wrong with logging in: no logged in user" +
                    " found!");
        }

        if (!isRequestHappening) {
            isRequestHappening = true;
            joinGroupRequest(Integer.toString(givenID), this);
        }
    }

    /**
     * This method should create a new group with the name given in create_group_input_text.
     * This means that this method should perform a request to create a new group with the given
     * name.
     * If the request returns an error,
     * this method should show a Toast displaying the error message.
     * If the request is successful, this method should open a new GroupScreen activity of the newly
     * created group and should finish this activity.
     * Since this method uses the LoggedInUser to check which user to add to the expense,
     * this method should check whether the singleton instance is defined,
     * and throw an exception otherwise.
     * @pre {@code {@link LoggedInUser#getInstance()} != null}
     * @throws RuntimeException if {@code {@link LoggedInUser#getInstance()} == null}
     * Event handler for the create group button
     * @param view The View instance of the button that was pressed
     */
    public void onCreateGroup(View view) {
        //todo explain variables
        EditText nameET = findViewById(R.id.create_group_input_text);
        String name = nameET.getText().toString();

        // Check if the Singleton class LoggedInUser is initialized
        if (LoggedInUser.getInstance() == null) {
            throw new RuntimeException("Something went wrong with logging in: no loggged in user" +
                    " found!");
        }

        if (!isRequestHappening) {
            isRequestHappening = true;

            createGroupRequest(name, this);
        }
    }

    private void joinGroupRequest(String expenseGroupID, Context context) {
        ExpenseGroupService.addToExpenseGroup(
                LoggedInUser.getInstance().getApiKey(),
                LoggedInUser.getInstance().getUser().getUsername(),
                expenseGroupID,
                context,
                new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        // Redirect to group screen
                        Intent groupScreenIntent = new Intent(context, GroupScreenActivity.class);
                        // Link the group ID
                        groupScreenIntent.putExtra(
                                "EXPENSE_GROUP_ID",
                                Integer.parseInt(expenseGroupID));
                        startActivity(groupScreenIntent);
                        finish();
                        // Update semaphore
                        isRequestHappening = false;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        GregService.showErrorToast(errorMessage, context);
                        isRequestHappening = false;
                    }
                });
    }

    private void createGroupRequest(String name, Context context) {
        ExpenseGroupService.createExpenseGroup(
                LoggedInUser.getInstance().getApiKey(),
                name,
                context,
                new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        // Redirect to group screen
                        Intent groupScreenIntent = new Intent(context, GroupScreenActivity.class);
                        // Link the group ID
//                        System.out.print(data);
                        groupScreenIntent.putExtra(
                                "EXPENSE_GROUP_ID",
                                Integer.parseInt(data));
                        startActivity(groupScreenIntent);
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
}