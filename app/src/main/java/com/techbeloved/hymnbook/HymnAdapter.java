package com.techbeloved.hymnbook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kennedy on 4/6/18.
 */

public class HymnAdapter extends RecyclerView.Adapter<HymnAdapter.ViewHolder> {

    // Provide direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView numberTextView, titleTextView;

        public ViewHolder(View itemView){
            super(itemView);
            numberTextView = itemView.findViewById(R.id.hymn_number);
            titleTextView = itemView.findViewById(R.id.hymn_title);
        }
    }

    // Store a member variable for the Hymns
    private List<Hymn> mHymns;
    // Store the cotext for easy access
    private Context mContext;

    public HymnAdapter(Context context, List<Hymn> hymns){
        mHymns = hymns;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext(){
        return mContext;
    }

    // Usually involves inflating a layout from xml and returning the holder
    @NonNull
    @Override
    public HymnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View hymnView = inflater.inflate(R.layout.hymn_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(hymnView);
        return viewHolder;
    }

    // Involves populateing data into the item through holder

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get data model based on position
        Hymn hymn = mHymns.get(position);

        // Set the item views based on your views and data model
        TextView numberTextView = holder.numberTextView;
        numberTextView.setText(hymn.getNumber() + ""); // Temporarily convert to string
        TextView titleTextView = holder.titleTextView;
        titleTextView.setText(hymn.getTitle());
    }

    /**
     * Returns the total count of items in the list
     */
    @Override
    public int getItemCount() {
        return mHymns.size();
    }


}
