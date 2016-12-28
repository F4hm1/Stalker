package com.avoupavou.stalker.location;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avoupavou.stalker.R;

import java.util.ArrayList;

/**
 * Created by Pantazis on 26-Dec-16.
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {
    private ArrayList<PlaceEntry> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceListAdapter(ArrayList<PlaceEntry> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlaceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaceEntry place = mDataset.get(position);
        holder.name_text.setText(place.getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_text;

        public ViewHolder(View v) {
            super(v);
            name_text = (TextView) v.findViewById(R.id.place_name_text);
        }
    }

}