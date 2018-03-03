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
    final private String YOUTUBE_BASE = "https://www.youtube.com/watch?v=";
    OnItemClickListener mListener;
    private List<Trailer> mTrailers;
    private LayoutInflater mInflater;
    private Context mContext;
    //Constructor
    TrailerAdapter(Context context, List<Trailer> trailers, OnItemClickListener listener) {
        mTrailers = trailers;
        mContext = context;
        mListener = listener;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // inflates the elements into the view
        final Trailer source = mTrailers.get(position);
        holder.mSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(source);
            }
        });
        // For learning reasons I will show the link, but I'll keep divided Youtube base URL and Video ID
        holder.mSource.setText(YOUTUBE_BASE + source.getSource());
    }

    //Interface for the ClickLIstener
    public interface OnItemClickListener {
        void onItemClick(Trailer trailer);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mSource;

        ViewHolder(View v) {
            super(v);
            mSource = v.findViewById(R.id.trailerLink);
        }
    }

}
