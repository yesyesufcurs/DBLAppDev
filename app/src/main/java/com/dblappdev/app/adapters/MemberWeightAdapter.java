package com.dblappdev.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dblappdev.app.R;
import com.dblappdev.app.dataClasses.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberWeightAdapter extends RecyclerView.Adapter<GregViewHolder> {

    // List of the users of the group
    private ArrayList<User> userList;
    // This map keeps track of how many times each user weighs in on the expense
    private HashMap<User, Integer> amountMap;

    // On click listeners for the plus and minus buttons
    private View.OnClickListener plusListener;
    private View.OnClickListener minusListener;

    /**
     * Extended version of the ViewHolder that adds support for the plus and minus buttons
     */
    public static class ViewHolder extends GregViewHolder {
        private final ImageButton plusButton;
        private final ImageButton minusButton;

        public ViewHolder(View view) {
            super(view);

            plusButton = (ImageButton) view.findViewById(R.id.plus_button);
            minusButton = (ImageButton) view.findViewById(R.id.minus_button);
        }

        public ImageButton getPlusButton() { return plusButton; }

        public ImageButton getMinusButton() { return minusButton; }

    }

    /**
     * Initialize the dataset of the adapter
     */
    public MemberWeightAdapter(View.OnClickListener plusListener, View.OnClickListener minusListener,
                               ArrayList<User> users, HashMap<User, Integer> amountMap) {

        this.userList = users;
        this.amountMap = amountMap;

        // Set the listeners
        this.plusListener = plusListener;
        this.minusListener = minusListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GregViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item_members, viewGroup, false);
        // Bind the on click listeners for the plus and minus buttons
        view.findViewById(R.id.plus_button).setOnClickListener(plusListener);
        view.findViewById(R.id.minus_button).setOnClickListener(minusListener);

        return new GregViewHolder(view);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GregViewHolder viewHolder, final int position) {
        // Get element from the dataset at the given position and replace the contents of the view
        viewHolder.getTextViewName().setText(userList.get(position).getUsername());
        int amount;
        try {
            amount = amountMap.get(userList.get(position));
        } catch (Exception e) {
            amount = 0;
        }
        viewHolder.getTextViewBalance().setText(Integer.toString(amount));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return userList.size(); }

}
