package com.dblappdev.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dblappdev.app.R;
import com.dblappdev.app.dataClasses.ExpenseGroup;
import com.dblappdev.app.dataClasses.LoggedInUser;

import java.util.ArrayList;

public class ExpenseGroupAdapter extends RecyclerView.Adapter<ExpenseGroupAdapter.ViewHolder> {

    // click event listener for the list entries
    private View.OnClickListener onClickListener;

    // dataset used in the recyclerview
    private ArrayList<ExpenseGroup> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewBalance;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textViewName = (TextView) view.findViewById(R.id.item_name);
            textViewBalance = (TextView) view.findViewById(R.id.item_balance);
        }

        public TextView getTextViewName() {
            return textViewName;
        }

        public TextView getTextViewBalance() {
            return textViewBalance;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     * @param listener View.OnClickListener that deals with the on click event
     * TODO: Update this to fit with the loading in of the data (data should probably be added as a parameter)
     */
    public ExpenseGroupAdapter(View.OnClickListener listener) {
        onClickListener = listener;

        // START TEMP CODE
        // generate mockup data
        localDataSet = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            localDataSet.add(new ExpenseGroup(i, "Group no. " + i, null));
        }
        // END TEMP CODE
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        view.setOnClickListener(onClickListener);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // TODO: Update mock data to check whether the grabbing of the balance actually works
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from the dataset at the given position and replace the contents of the view
        viewHolder.getTextViewName().setText(localDataSet.get(position).getTitle());
        String balanceString;
        try {
            float balance = localDataSet.get(position).getBalance().get(LoggedInUser.getInstance().getUser());
            balanceString = "€" + balance;
        } catch (Exception e) {
            balanceString = "€--,--";
        }
        viewHolder.getTextViewBalance().setText(balanceString);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
