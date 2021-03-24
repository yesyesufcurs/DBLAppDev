package com.dblappdev.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.dblappdev.app.R;
import com.dblappdev.app.dataClasses.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberBalanceAdapter extends RecyclerView.Adapter<GregViewHolder> {

    // List of the users of the group
    private ArrayList<User> userList;
    // This map keeps track of the balance of each user in the group
    private HashMap<User, Float> balanceMap;

    private View.OnClickListener removeListener;

    /**
     * Extended version of the ViewHolder that adds support for the remove button
     */
    public static class ViewHolder extends GregViewHolder {
        private final ImageButton removeButton;

        public ViewHolder(View view) {
            super(view);

            removeButton = (ImageButton) view.findViewById(R.id.removeButton);
        }

        public ImageButton getRemoveButton() { return removeButton; }
    }

    /**
     * Initialize the dataset of the adapter
     * TODO: Add data needed for this as a parameter instead of using mockup data
     */
    public MemberBalanceAdapter(View.OnClickListener removeListener, ArrayList<User> userList,
                                HashMap<User, Float> balanceMap) {
        this.removeListener = removeListener;
        this.userList = userList;
        this.balanceMap = balanceMap;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GregViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item_memberlist, viewGroup, false);
        view.findViewById(R.id.removeButton).setOnClickListener(removeListener);
        return new GregViewHolder(view);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GregViewHolder viewHolder, final int position) {
        // Get element from the dataset at the given position and replace the contents of the view
        viewHolder.getTextViewName().setText(userList.get(position).getUsername());
        String balanceString;
        try {
            float balance = balanceMap.get(userList.get(position));
            balanceString = "€" + balance;
        } catch (Exception e) {
            balanceString = "€--,--";
        }
        viewHolder.getTextViewBalance().setText(balanceString);
        viewHolder.getView().findViewById(R.id.removeButton).setTag(userList.get(position).getUsername());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return userList.size(); }

}
