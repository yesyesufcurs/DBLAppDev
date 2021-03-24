package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectMembersActivity extends AppCompatActivity {

    boolean isRequestHappening = false;
    int expenseGroupId;
    String title;
    float amount;
    String imagePath;
    int EXPENSE_ID;
    String MODE;
    private ArrayList<User> users;
    private HashMap<User, Integer> amountMap;
    Context currentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_members);

        currentContext = this;

        if (LoggedInUser.getInstance() == null) {
            throw new RuntimeException("Something went wrong with logging in: no loggged in user" +
                    " found upon creation of the home screen!");
        }

        Bundle bundle = getIntent().getExtras();
        if (!getIntent().hasExtra("expenseGroupId")) {
            throw new RuntimeException("Something went wrong with opening the expense group: no " +
                    "expense group selected.");
        }

        if (!getIntent().hasExtra("price")) {
            throw new RuntimeException("Something went wrong with opening the expense group: no " +
                    "price.");
        }

        if (!getIntent().hasExtra("title")) {
            throw new RuntimeException("Something went wrong with opening the expense group: no " +
                    "title.");
        }

        if (!getIntent().hasExtra("imagePath")) {
            throw new RuntimeException("Something went wrong with opening the expense group: no " +
                    "image.");
        }

        if (!getIntent().hasExtra("MODE")) {
            throw new RuntimeException("Something went wrong with opening the expense group: no " +
                    "mode.");
        }

        MODE = bundle.getString("MODE");

        if (MODE.equals("EDIT")) {
            if (!getIntent().hasExtra("EXPENSE_ID")) {
                throw new RuntimeException("Something went wrong with opening the expense group: no " +
                        "EXPENSE_ID.");
            }
        }

        title = bundle.getString("title");
        expenseGroupId = bundle.getInt("expenseGroupId");
        String priceString = bundle.getString("price");
        amount = Float.parseFloat(priceString);
        imagePath = bundle.getString("imagePath");
        if (MODE.equals("EDIT")) {
            EXPENSE_ID = bundle.getInt("EXPENSE_ID");
        }

        if (!isRequestHappening) {
            // Update semaphore
            isRequestHappening = true;
            // This method will also deal with the instantiating of the recycler view
            getUsers(this);
        }
    }

    /**
     * Event handler for the back button
     *
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {
        finish();
    }

    /**
     * Event handler for the back button
     *
     * @param view The View instance of the button that was pressed
     */
    public void onCheckmark(View view) {
        int totalAmount = 0;
        for (User user : amountMap.keySet()) {
            totalAmount += amountMap.get(user);
        }

        JSONObject expenseIOU = new JSONObject();
        if (totalAmount != 0) {
            try {
                for (User user : amountMap.keySet()) {
                    expenseIOU.put(user.getUsername(), amount * amountMap.get(user) / totalAmount);
                }
            } catch (JSONException e) {
                throw new IllegalStateException("Cannot set expenseIOUJSON");
            }
        }

        Bitmap bmp = imagePath == null ? null : BitmapFactory.decodeFile(imagePath);
        if (MODE.equals("ADD")) {
            APIService.createExpense(LoggedInUser.getInstance().getApiKey(), LoggedInUser.getInstance().getUser().getUsername(), title, "" + (Math.round(amount * 100.0f) / 100.0f), bmp, "Description", "" + expenseGroupId, this,
                    new APIResponse<String>() {
                        @Override
                        public void onResponse(String data) {
                            System.out.println(data);
                            ExpenseDetailsActivity.currentContext.finish();
                            // Redirect to the group screen
                            finish();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error, String errorMessage) {
                            GregService.showErrorToast(errorMessage, currentContext);
                        }
                    });
        } else {
            APIService.modifyExpense(LoggedInUser.getInstance().getApiKey(), title, "" + (Math.round(amount * 100.0f) / 100.0f), bmp, "Description", "" + expenseGroupId, ""+EXPENSE_ID,this,
                    new APIResponse<String>() {
                        @Override
                        public void onResponse(String data) {
                            System.out.println(data);
                            ExpenseDetailsActivity.currentContext.finish();
                            // Redirect to the group screen
                            finish();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error, String errorMessage) {
                            GregService.showErrorToast(errorMessage, currentContext);
                        }
                    });
        }


    }

    /**
     * Event handler for the plus buttons in the recyclerview
     *
     * @param view The View instance of the member list entry that was pressed
     */
    public void onPlusClick(View view) {
        for (User user : users) {
            if (user.getUsername().equals(view.getTag())) {
                amountMap.put(user, amountMap.get(user) + 1);

                ViewGroup vg = (ViewGroup) view.getParent().getParent().getParent();
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View v = vg.getChildAt(i);
                    if (v.getId() == R.id.item_balance) {
                        ((TextView) v).setText(Integer.toString(amountMap.get(user)));
                    }
                }
                break;
            }
        }
    }

    /**
     * Event handler for the minus buttons in the recyclerview
     *
     * @param view The View instance of the member list entry that was pressed
     */
    public void onMinusClick(View view) {
        for (User user : users) {
            if (user.getUsername().equals(view.getTag())) {
                if (amountMap.get(user) > 0) {
                    amountMap.put(user, amountMap.get(user) - 1);

                    ViewGroup vg = (ViewGroup) view.getParent().getParent().getParent();
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        View v = vg.getChildAt(i);
                        if (v.getId() == R.id.item_balance) {
                            ((TextView) v).setText(Integer.toString(amountMap.get(user)));
                        }
                    }
                }
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
