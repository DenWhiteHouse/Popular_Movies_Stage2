package com.example.android.popular_movies_stage2;


import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popular_movies_stage2.MoviesDBContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by casab on 24/02/2018.
 */

public class MovieDetails extends AppCompatActivity {
    MovieContentProvider mMovieContentProvider;

    TrailerAdapter mTrailerAdapter;
    Button mFavButton;
    TextView mIsFavTV;
    ReviewAdapter mReviewAdapter;
    Movie mMovie;
    private int POSTERSIZEw = 185;
    private int POSTERSIZEh = 270;

    //Implicit Intent to Watch the Trailer on YouTube
    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
        //Passing the local context instread of MovideDtails.this needs the FLAG_ACTIVITY_NEW TASK( is this what you want) to start the intent
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviedetails);
        //Setting the FavoButton
        mFavButton = findViewById(R.id.buttonFav);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFavouriteMovie(mMovie);
                mFavButton.setVisibility(View.GONE);
                mIsFavTV.setVisibility(View.VISIBLE);
            }
        });

        TextView OriginalTitleTV = findViewById(R.id.originalTitle);
        ImageView ImageIV = findViewById(R.id.movieImage);
        TextView SynopsysTV = findViewById(R.id.synopsis);
        TextView RatingTV = findViewById(R.id.userRating);
        TextView ReleaseDateTV = findViewById(R.id.releaseDate);
        // Starting the Layout the Favourite Message is not visible
        mIsFavTV = findViewById(R.id.isYourFavourite);

        //Some scholarship mate suggested me to user Parcelable, but as first project I preferred to
        // get more practise with simple intents

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getResources().getString(R.string.intent_movie));
        mMovie = movie;

        // If the Movie is A favourite Movie Don't Show the Button to add as Favourite but a Message instead
        // The User can Unfavoruite the Movie from the Favourite SortLayout on the MainActivity
        if ((mMovie.mIsFavourireFLAG) == 1) {
            mFavButton.setVisibility(View.GONE);
            mIsFavTV.setVisibility(View.VISIBLE);
        }

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
                mTrailerAdapter = new TrailerAdapter(context, trailersFields, new TrailerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Trailer trailer) {
                        watchYoutubeVideo(context, trailer.mSource);
                    }
                });
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

    private void saveFavouriteMovie(Movie mMovie) {
        Toast.makeText(MovieDetails.this, R.string.favouriteSaveClicked, Toast.LENGTH_SHORT).show();

        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOrginalTitle());
        values.put(MovieEntry.COLUMN_POSTER_PATH, mMovie.getImage());
        values.put(MovieEntry.COLUMN_OVERVIEW, mMovie.getSynopsis());
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getRating());
        values.put(MovieEntry.COLUMN_RELEASE_DATE, mMovie.getMovieReleaseDate());
        values.put(MovieEntry.COLUMN_MOVIE_ID, mMovie.getMovieID());
        getContentResolver().insert(MovieEntry.CONTENT_URI, values);

        //TODO: Store Trailers and Reviews as well, checking for the connection, changing the details layout adding (un)visible messages
    }
}
