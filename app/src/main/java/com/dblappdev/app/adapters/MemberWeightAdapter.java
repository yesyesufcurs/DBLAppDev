package com.dblappdev.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dblappdev.app.R;
import com.dblappdev.app.dataClasses.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberWeightAdapter extends RecyclerView.Adapter<MemberWeightAdapter.ViewHolder> {

    // List of the users of the group
    private ArrayList<User> userList;
    // This map keeps track of how many times each user weighs in on the expense
    private HashMap<User, Integer> amountMap;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewAmount;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textViewName = (TextView) view.findViewById(R.id.item_name);
            textViewAmount = (TextView) view.findViewById(R.id.item_balance);
        }

        public TextView getTextViewName() { return textViewName; }
        public TextView getTextViewAmount() { return textViewAmount; }
    }

    /**
     * Initialize the dataset of the adapter
     * TODO: Update this to fit the loading in of the data, which should be added as a parameter
     */
    public MemberWeightAdapter() {
        // START TEMP CODE
        // generate mockup data
        userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(new User("User no. " + i, i + "@" + i + ".com"));
        }
        amountMap = new HashMap<>();
        for (User user : userList) {
            amountMap.put(user, 1);
        }
        // END TEMP CODE
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item_members, viewGroup, false);
        return new ViewHolder(view);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from the dataset at the given position and replace the contents of the view
        viewHolder.getTextViewName().setText(userList.get(position).getUsername());
        int amount;
        try {
            amount = amountMap.get(userList.get(position));
        } catch (Exception e) {
            amount = 0;
        }
        viewHolder.getTextViewAmount().setText(Integer.toString(amount));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return userList.size(); }

}
