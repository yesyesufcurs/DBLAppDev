package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;
import com.dblappdev.app.adapters.ExpenseAdapter;
import com.dblappdev.app.adapters.ExpenseGroupAdapter;
import com.dblappdev.app.adapters.MemberWeightAdapter;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;
import com.dblappdev.app.dataClasses.Expense;
import com.dblappdev.app.dataClasses.LoggedInUser;
import com.dblappdev.app.dataClasses.User;
import com.dblappdev.app.gregservice.GregService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectMembersActivity extends AppCompatActivity {

    boolean isRequestHappening = false;
    int expenseGroupId;
    private ArrayList<User> users;
    private HashMap<User, Integer> amountMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_members);

        if (LoggedInUser.getInstance() == null) {
            throw new RuntimeException("Something went wrong with logging in: no loggged in user" +
                    " found upon creation of the home screen!");
        }

        Bundle bundle = getIntent().getExtras();
        if (!getIntent().hasExtra("expenseGroupId")) {
            throw new RuntimeException("Something went wrong with opening the expense group: no " +
                    "expense group selected.");
        }

        expenseGroupId = bundle.getInt("expenseGroupId");

        if (!isRequestHappening) {
            // Update semaphore
            isRequestHappening = true;
            // This method will also deal with the instantiating of the recycler view
            getUsers(this);
        }
    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {
        finish();
    }

    /**
     * Event handler for the back button
     * @param view The View instance of the button that was pressed
     */
    public void onCheckmark(View view) {

        ExpenseDetailsActivity.currentContext.finish();
        // Redirect to the group screen
        finish();

    }

    /**
     * Event handler for the plus buttons in the recyclerview
     * @param view The View instance of the member list entry that was pressed
     */
    public void onPlusClick(View view) {
        for (User user : users) {
            if (user.getUsername().equals(view.getTag())) {
                //amountMap.put(user, amountMap.get(user) + 1);
                break;
            }
        }
    }

    /**
     * Event handler for the minus buttons in the recyclerview
     * @param view The View instance of the member list entry that was pressed
     */
    public void onMinusClick(View view) {
        for (User user : users) {
            if (user.getUsername().equals(view.getTag())) {
                //amountMap.put(user, amountMap.get(user) - 1);
                break;
            }
        }
    }

    public void getUsers(Context context) {
        APIService.getExpenseGroupMembers(LoggedInUser.getInstance().getApiKey(),
                Integer.toString(expenseGroupId), context,
                new APIResponse<List<Map<String, String>>>() {
                    @Override
                    public void onResponse(List<Map<String, String>> data) {
                        // Parse the data into the expenses ArrayList
                        users = new ArrayList<User>();
                        for (Map<String, String> group : data) {
                            User user = new User(group.get("user_id"));
                            users.add(user);
                        }
                        amountMap = new HashMap<>();
                        for (User user : users) {
                            if (user.getUsername().equals(LoggedInUser.getInstance().getUser().getUsername())) {
                                amountMap.put(user, 0);
                            } else {
                                amountMap.put(user, 1);
                            }
                        }
                        // Set the recyclerview and its settings
                        RecyclerView recView = (RecyclerView) findViewById(R.id.recyclerViewMembers);
                        View.OnClickListener plusListener = view -> onPlusClick(view);
                        View.OnClickListener minusListener = view -> onMinusClick(view);
                        MemberWeightAdapter adapter = new MemberWeightAdapter(plusListener, minusListener,
                                users, amountMap);
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
