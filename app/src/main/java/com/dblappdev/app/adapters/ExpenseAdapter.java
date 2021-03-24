package com.dblappdev.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dblappdev.app.R;
import com.dblappdev.app.dataClasses.Expense;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<GregViewHolder> {

    // click event listener for the list entries
    private View.OnClickListener onClickListener;

    // dataset used in the recyclerview
    private ArrayList<Expense> localDataSet;

    /**
     * Initialize the dataset of the adapter
     * @param listener View.OnClickListener that deals with the on click event
     * TODO: Update this to fit with the loading in of the data (should be added as a parameter)
     */
    public ExpenseAdapter(View.OnClickListener listener, ArrayList<Expense> expenses) {
        onClickListener = listener;
        localDataSet = expenses;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GregViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        view.setOnClickListener(onClickListener);

        return new GregViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GregViewHolder viewHolder, final int position) {
        // Get element from the dataset at the given position and replace the contents of the view
        viewHolder.getTextViewName().setText(localDataSet.get(position).getDescription());
        viewHolder.getTextViewBalance().setText("€" + localDataSet.get(position).getAmount());
        viewHolder.getView().setTag(localDataSet.get(position).getId());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return localDataSet.size(); }

}
