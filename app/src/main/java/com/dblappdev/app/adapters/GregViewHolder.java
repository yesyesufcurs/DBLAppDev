package com.dblappdev.app.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dblappdev.app.R;

/**
 * This class is a reference to the type of views that are being used in the recyclerViews
 * throughout the application
 */
public class GregViewHolder extends RecyclerView.ViewHolder {
    private final TextView textViewName;
    private final TextView textViewBalance;

    public GregViewHolder(View view) {
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
