package com.dblappdev.app.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dblappdev.app.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    // START TEMP CODE

    public class TempDataObject {
        private String name;
        private float balance;

        public TempDataObject(String name, float balance) {
            this.name = name;
            this.balance = balance;
        }

        public String getName() { return name; }

        public float getBalance() { return balance; }
    }

    private TempDataObject[] localDataSet;

    // END TEMP CODE

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
     * TODO: Update this to fit with the eventual classes
     */
    public RecyclerViewAdapter() {
        localDataSet = new TempDataObject[20];
        for (int i = 0; i < localDataSet.length; i++) {
            localDataSet[i] = new TempDataObject("Item name " + i, 0.1f * i);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // TODO: Update this to fit with the eventual classes
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from the dataset at the given position and replace the contents of the view
        viewHolder.getTextViewName().setText(localDataSet[position].getName());
        viewHolder.getTextViewBalance().setText("€" + localDataSet[position].getBalance());
    }

    // Return the size of your dataset (invoked by the layout manager)
    // TODO: Update this to fit with the eventual classes
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

}
