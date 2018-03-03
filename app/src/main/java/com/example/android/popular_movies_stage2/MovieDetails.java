package com.example.android.popular_movies_stage2;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by casab on 24/02/2018.
 */

public class MovieDetails extends AppCompatActivity {


    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;
    private int POSTERSIZEw = 185;
    private int POSTERSIZEh = 270;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviedetails);
        //Bonding class variables to views
        TextView OriginalTitleTV = (TextView) findViewById(R.id.originalTitle);
        ImageView ImageIV = (ImageView) findViewById(R.id.movieImage);
        TextView SynopsysTV = (TextView) findViewById(R.id.synopsis);
        TextView RatingTV = (TextView) findViewById(R.id.userRating);
        TextView ReleaseDateTV = (TextView) findViewById(R.id.releaseDate);

        //Some scholarship mate suggested me to user Parcelable, but as first project I preferred to
        // get more practise with simple intents

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getResources().getString(R.string.intent_movie));

        // The movie object as Parceable from the intent contains the information to be used
        // to populate the UI of Details
        // Setting the Image using the URL of the image with Picasso as suggested on the Project guide

        Picasso.with(this)
                .load(movie.getImage())
                .resize(POSTERSIZEw, POSTERSIZEh)
                .into(ImageIV);

        OriginalTitleTV.setText(movie.getOrginalTitle());
        SynopsysTV.setText(movie.getSynopsis());
        RatingTV.setText(movie.getRating());
        ReleaseDateTV.setText(movie.getMovieReleaseDate());

        //Methods for STAGE 2 - Background Fetching Trailers and Review
        //OnCompleted Async tasks inflate data to RecyclerViews on DetailsActivity
        inflateTrailers(getApplicationContext(), movie.movieID);
        inflateReviews(getApplicationContext(), movie.movieID);
    }

    private void inflateTrailers(final Context context, final String movieID) {
        // Setting the adapters Fetching the Sources
        final RecyclerView recyclerView = findViewById(R.id.trailerRecylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        // Put the fields of Trailers parsed obect to an ArrayList to inflate the RecylceView
        final List<Trailer> trailersFields = new ArrayList<>();
        //Set Interfaces and Async Fetching
        OnFetchTrailersCompleted taskCompleted = new OnFetchTrailersCompleted() {
            @Override
            public void onUtilsAsyncTaskCompleted(Trailer[] trailers) {
                //Trailers Allocation to View
                for (int i = 0; i < trailers.length; i++) {
                    trailersFields.add(i, trailers[i]);
                }
                //Adapter
                mTrailerAdapter = new TrailerAdapter(context, trailersFields);
                recyclerView.setAdapter(mTrailerAdapter);
            }
        };
        // Fetch Trailers AsyncTask
        FetchTrailers fetchTrailers = new FetchTrailers(taskCompleted);
        fetchTrailers.execute(movieID);
    }

    private void inflateReviews(final Context context, final String movieID) {
        // Setting the adapters Fetching the Sources
        final RecyclerView recyclerView = findViewById(R.id.reviewRecylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        // Put the fields of Trailers parsed obect to an ArrayList to inflate the RecylceView
        final List<Review> reviewsFields = new ArrayList<>();
        //Set Interfaces and Async Fetching
        OnFetchReviewsCompleted taskCompleted = new OnFetchReviewsCompleted() {
            @Override
            public void onUtilsAsyncTaskCompleted(Review[] reviews) {
                //Trailers Allocation to View
                for (int i = 0; i < reviews.length; i++) {
                    reviewsFields.add(i, reviews[i]);
                }
                //Adapter
                mReviewAdapter = new ReviewAdapter(context, reviewsFields);
                recyclerView.setAdapter(mReviewAdapter);
            }
        };
        // Fetch Trailers AsyncTask
        FetchReviews fetchReviews = new FetchReviews(taskCompleted);
        fetchReviews.execute(movieID);
    }
}
