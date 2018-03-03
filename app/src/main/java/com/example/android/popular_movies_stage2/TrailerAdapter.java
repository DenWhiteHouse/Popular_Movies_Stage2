package com.example.android.popular_movies_stage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by casab on 02/03/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private List<Trailer> mTrailers;
    private LayoutInflater mInflater;
    private Context mContext;

    //Constructor
    TrailerAdapter(Context context, List<Trailer> trailers) {
        mTrailers = trailers;
        mContext = context;
    }

    // Create new views
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mInflater = LayoutInflater.from(context);
        // create a new view
        View v = mInflater.inflate(R.layout.trailer_listitem, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewholder = new ViewHolder(v);
        return viewholder;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // inflates the elements into the view
        Trailer source = mTrailers.get(position);
        holder.mSource.setText(source.getSource());
    }

    public void add(int position, Trailer source) {
        mTrailers.add(position, source);
    }

    public Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mSource;

        ViewHolder(View v) {
            super(v);
            mSource = v.findViewById(R.id.trailerLink);
        }
    }
}
