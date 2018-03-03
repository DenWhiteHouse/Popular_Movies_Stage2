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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> mReviews;
    private LayoutInflater mInflater;
    private Context mContext;

    //Constructor
    ReviewAdapter(Context context, List<Review> reviews) {
        mReviews = reviews;
        mContext = context;
    }

    // Create new views
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mInflater = LayoutInflater.from(context);
        // create a new view
        View v = mInflater.inflate(R.layout.review_listitem, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ReviewAdapter.ViewHolder viewholder = new ReviewAdapter.ViewHolder(v);
        return viewholder;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        // inflates the elements into the view
        Review review = mReviews.get(position);
        holder.mAuthur.setText(review.getAuthur());
        holder.mReview.setText(review.getReview());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mAuthur;
        TextView mReview;

        ViewHolder(View v) {
            super(v);
            mAuthur = v.findViewById(R.id.authurTV);
            mReview = v.findViewById(R.id.reviewTV);
        }
    }

    public Context getContext() {
        return mContext;
    }
}