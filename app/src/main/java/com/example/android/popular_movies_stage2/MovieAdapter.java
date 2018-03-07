package com.example.android.popular_movies_stage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by casab on 07/03/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    AdapterView.OnItemClickListener mListener;
    Context mContext;
    RecyclerView mRecyclerView;
    MovieContentProvider mMovieContentProvider;
    private List<Movie> mMovieFav;
    private LayoutInflater mInflater;
    private int POSTERSIZEw = 370;
    private int POSTERSIZEh = 540;

    public MovieAdapter(Context context, List<Movie> movies, RecyclerView recyclerView) {
        mMovieFav = movies;
        mContext = context;
        mRecyclerView = recyclerView;
    }

    // Create new views
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = mContext;
        mInflater = LayoutInflater.from(context);
        // create a new view
        View v = mInflater.inflate(R.layout.favourite_movies_posteritem, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MovieAdapter.ViewHolder viewholder = new MovieAdapter.ViewHolder(v);
        return viewholder;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mMovieFav.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // inflates the elements into the view
        final Movie movie = mMovieFav.get(position);

        Picasso.with(mContext)
                .load(movie.getImageNoBasePath())
                .resize(POSTERSIZEw, POSTERSIZEh) // FIXED BOX SIZE
                .into(holder.mPoster);
        holder.mButton.setText(R.string.UnFavourite);
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMovieContentProvider = new MovieContentProvider();
                mMovieContentProvider.mDBHelper = new MovieDBHelper(mContext);
                mMovieContentProvider.deleteSingleMovie(MoviesDBContract.MovieEntry.CONTENT_URI, new String[]{movie.getMovieID()});
                Toast.makeText(mContext, R.string.UnFavourite + movie.getMovieID(), Toast.LENGTH_SHORT).show();
                updateView(position);
            }
        });
        holder.mSynopnsis.setText(movie.getSynopsis());
        holder.mTitle.setText(movie.getOrginalTitle());
    }

    public void updateView(int position) {
        mMovieFav.remove(position);
        MovieAdapter newAdapter = new MovieAdapter(mContext, mMovieFav, mRecyclerView);
        mRecyclerView.setAdapter(newAdapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mPoster;
        Button mButton;
        TextView mTitle;
        TextView mSynopnsis;

        ViewHolder(View v) {
            super(v);
            mPoster = v.findViewById(R.id.posterFavMovie);
            mButton = v.findViewById(R.id.unfavButton);
            mTitle = v.findViewById(R.id.originalTitleFav);
            mSynopnsis = v.findViewById(R.id.synopsisFav);
        }
    }
}
