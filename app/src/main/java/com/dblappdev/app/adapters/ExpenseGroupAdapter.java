package com.dblappdev.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dblappdev.app.R;
import com.dblappdev.app.dataClasses.ExpenseGroup;
import com.dblappdev.app.dataClasses.LoggedInUser;

import java.util.ArrayList;

public class ExpenseGroupAdapter extends RecyclerView.Adapter<GregViewHolder> {

    // click event listener for the list entries
    private View.OnClickListener onClickListener;

    // dataset used in the recyclerview
    private ArrayList<ExpenseGroup> localDataSet;

    /**
     * Initialize the dataset of the Adapter
     * @param listener View.OnClickListener that deals with the on click event
     * @param dataset ArrayList that contains the ExpenseGroups to be shown in the view
     */
    public ExpenseGroupAdapter(View.OnClickListener listener, ArrayList<ExpenseGroup> dataset) {
        onClickListener = listener;
        localDataSet = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GregViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.recycler_view_item,
                viewGroup,
                false);
        view.setOnClickListener(onClickListener);

        return new GregViewHolder(view);
    }

    /**
     * This method should replace the contents of a view (invoked by the layout manager)
     * This means it should set {@link GregViewHolder#getTextViewName()} to the
     * {@link ExpenseGroup#getTitle()} and
     *  set {@link GregViewHolder#getTextViewBalance()} to '(mod)'
     * if and only if the currently logged in user is the moderator of the ExpenseGroup.
     * Lastly, it should also link the {@link ExpenseGroup#getId()} with the view such that it can
     * be used in the onClickListener of the ViewHolder.
     * @param viewHolder View holder of the view whose contents have to be replaced
     * @param position index of the dataset that is to be used to set the data
     */
    @Override
    public void onBindViewHolder(GregViewHolder viewHolder, final int position) {
        // Get the name of the ExpenseGroup
        ExpenseGroup group = localDataSet.get(position);
        String nameString = group.getTitle();

        // Check whether the logged in user is the moderator of the group
        String loggedInUsername;
        try {
           loggedInUsername = LoggedInUser.getInstance().getUser().getUsername();
        } catch(Exception e) {
            // If no user is logged in, they obviously cannot be the moderator
            loggedInUsername = "";
        }
        String modUsername = group.getModerator().getUsername();
        // If the logged in user is the moderator, add the (mod) text on the view
        String moderatorString = loggedInUsername.equals(modUsername) ? "(mod)" : "";

        // Set the name and moderator text on the view
        viewHolder.getTextViewName().setText(nameString);
        viewHolder.getTextViewBalance().setText(moderatorString);

        // Link the ExpenseGroup with the view
        viewHolder.getView().setTag(group.getId());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
