package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;
import com.dblappdev.app.adapters.ExpenseGroupAdapter;
import com.dblappdev.app.adapters.MemberWeightAdapter;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;
import com.dblappdev.app.dataClasses.LoggedInUser;

public class SelectMembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_members);

        // Set the recyclerview and its settings
        RecyclerView recView = (RecyclerView) findViewById(R.id.recyclerViewMembers);
        View.OnClickListener plusListener = view -> onPlusClick(view);
        View.OnClickListener minusListener = view -> onMinusClick(view);
        MemberWeightAdapter adapter = new MemberWeightAdapter(plusListener, minusListener);
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
        //unpack the bytearray back to the image
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //upload picture
        // FIXME hardcoded test
//        APIService.createExpense(
//                LoggedInUser.getInstance().getApiKey(),
//                LoggedInUser.getInstance().getUser().getUsername(),
//                "hond",
//                "5",
//                bmp,
//                "there is a dog",
//                "178371",
//                this,
//                new APIResponse<String>() {
//                    @Override
//                    public void onResponse(String data) {
//                        System.out.println("picture has been uploaded, data: " + data);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error, String errorMessage) {
//                        System.out.println("error message: " + errorMessage);
//                    }
//                }

        );

        // Redirect to the group screen
        finish();
    }

    /**
     * Event handler for the plus buttons in the recyclerview
     * @param view The View instance of the member list entry that was pressed
     */
    public void onPlusClick(View view) {

    }

    /**
     * Event handler for the minus buttons in the recyclerview
     * @param view The View instance of the member list entry that was pressed
     */
    public void onMinusClick(View view) {

    }
}
