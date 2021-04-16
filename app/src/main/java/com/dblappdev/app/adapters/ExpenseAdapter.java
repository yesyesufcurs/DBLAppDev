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
    private View.OnClickListener onRemoveListener;

    // dataset used in the recyclerview
    private ArrayList<Expense> localDataSet;

    /**
     * Extended version of the ViewHolder that adds support for the remove button
     */
//    public static class ViewHolder extends GregViewHolder {
//        private final ImageButton removeButton;
//
//        public ViewHolder(View view) {
//            super(view);
//
//            removeButton = (ImageButton) view.findViewById(R.id.removeButton);
//        }
//
//        public ImageButton getRemoveButton() { return removeButton; }
//    }

    /**
     * Initialize the dataset of the adapter
     */
    public ExpenseAdapter(
            View.OnClickListener clickListener,
            View.OnClickListener removeListener,
            ArrayList<Expense> expenses) {
        onClickListener = clickListener;
        onRemoveListener = removeListener;
        localDataSet = expenses;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GregViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.recycler_view_item_memberlist,
                viewGroup,
                false);
        view.setOnClickListener(onClickListener);
        view.findViewById(R.id.removeButton).setOnClickListener(onRemoveListener);

        return new GregViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GregViewHolder viewHolder, final int position) {
        // Get element from the dataset at the given position and replace the contents of the view
        viewHolder.getTextViewName().setText(localDataSet.get(position).getDescription());
        viewHolder.getTextViewBalance().setText("€" + localDataSet.get(position).getAmount());
        viewHolder.getView().setTag(localDataSet.get(position).getId());
        viewHolder.getView().findViewById(
                R.id.removeButton).setTag(localDataSet.get(position).getId());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return localDataSet.size(); }

}
