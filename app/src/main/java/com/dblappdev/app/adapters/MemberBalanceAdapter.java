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

public class MemberBalanceAdapter extends RecyclerView.Adapter<MemberBalanceAdapter.ViewHolder> {

    // List of the users of the group
    private ArrayList<User> userList;
    // This map keeps track of the balance of each user in the group
    private HashMap<User, Float> balanceMap;

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
     * TODO: Add data needed for this as a parameter instead of using mockup data
     */
    public MemberBalanceAdapter() {
        // START TEMP CODE
        // generate mockup data
        userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(new User("User no. " + i, i + "@" + i + ".com"));
        }
        balanceMap = new HashMap<>();
        for (User user : userList) {
            balanceMap.put(user, 1.0f);
        }
        // END TEMP CODE
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from the dataset at the given position and replace the contents of the view
        viewHolder.getTextViewName().setText(userList.get(position).getUsername());
        String balanceString;
        try {
            float balance = balanceMap.get(userList.get(position));
            balanceString = "€" + balance;
        } catch (Exception e) {
            balanceString = "€--,--";
        }
        viewHolder.getTextViewAmount().setText(balanceString);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return userList.size(); }

}
