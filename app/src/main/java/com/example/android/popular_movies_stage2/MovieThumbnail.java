package com.example.android.popular_movies_stage2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by casab on 25/02/2018.
 */

// As the user can click on the movie Thumbnail to open an activity with the information of the movie
// received from the API as an Array stored in the Array Movie[] of Movie objects the clickable
//Image should be defined as a class to make possible the actions by being defined as class that
//can help the mainactivity to easly pass with a Parceable intent the information needed for the Details Activity

// Suggested usage of BaseAdapter https://developer.android.com/reference/android/widget/BaseAdapter.html
public class MovieThumbnail extends BaseAdapter {
    //Size of the Image Thumbnail selected for this project
    private int THUMBSIZEw = 185;
    private int THUMBSIZEh = 270;

    private final Context mContext;
    private final Movie[] mMovie;

    // Costructor take the movie element and the context
    public MovieThumbnail(Context context, Movie[] movie) {
        mContext = context;
        mMovie = movie;
    }

    // BaseAdapter needs a getView Method

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //cast the View to ImageView to be used with the util Picasso
        ImageView imageView;
        // Check if is the first time the view is being used
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mMovie[position].getImage())
                .resize(THUMBSIZEw, THUMBSIZEh) // FIXED BOX SIZE
                .into(imageView);
        return imageView;
    }

    // The following are the help methods of the class that facilitate the
    // actions of the main activity

    @Override
    public int getCount() {
        return mMovie.length;
    }

    @Override
    public Movie getItem(int position) {
        return mMovie[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
