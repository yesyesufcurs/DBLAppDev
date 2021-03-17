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
    public ExpenseAdapter(View.OnClickListener listener) {
        onClickListener = listener;

        // START TEMP CODE
        // generate mockup data
        localDataSet = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            localDataSet.add(new Expense(i, i, null, 0.01f * i, "Expense no. " + i, "Description no. " + i));
        }
        // END TEMP CODE
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
        viewHolder.getTextViewName().setText(localDataSet.get(position).getTitle());
        viewHolder.getTextViewBalance().setText("€" + localDataSet.get(position).getAmount());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() { return localDataSet.size(); }

}
