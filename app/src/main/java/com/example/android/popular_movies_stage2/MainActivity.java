package com.example.android.popular_movies_stage2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SORTBYFAV = 3;
    //SORT METHODS STATIC ID KEYS
    private final int SORTBYPOPULARITY = 1;
    private final int SORTBYRATE = 2;
    MovieAdapter mMovieAdapter;
    private Movie[] mFavouriteMovies;
    //When a thumbnail is clicked open the details activity
    private final GridView.OnItemClickListener movieThumbClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //take the movie to pass from the Array based on the position
            Movie movie = (Movie) parent.getItemAtPosition(position);
            //Check if the movie is a Favourire Movie
            if (CheckIfFavourite(movie)) {
                movie.setFlag(true);
            }
            //Sends an intent to the Deatail Activity with the selected movie object as a Parceable Extra
            Intent intent = new Intent(getApplicationContext(), MovieDetails.class);
            intent.putExtra(getResources().getString(R.string.intent_movie), movie);
            startActivity(intent);
        }
    };
    //KEYVariable for Sort Preference SHARED PREFERENCE
    private String SORTPREFERENCE = "/popular";
    //SORT METHODS ENDPOINTS
    private String SORTBYPOPENDPOINT = "/popular";
    private String SORTBYRATEENDPOINT = "/top_rated";
    // Size for Favourite Posters
    private int THUMBSIZEw = 185;
    private int THUMBSIZEh = 270;
    //The main activity should show a GridView and an Menu to set the order
    // As the options to sort the Grid are very few and don't determinate deep APP changes I'd go for SharedPreferences
    private GridView mGridView;
    private RecyclerView mRecyclerView;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set the view and the listener to the movie image ( if clicked opens the details activity)
        mGridView = findViewById(R.id.GridView);
        mGridView.setOnItemClickListener(movieThumbClickListener);
        mRecyclerView = findViewById(R.id.favouriteRecylerViev);

        //Checks if the app is re-starting from an instanceState
        if (savedInstanceState == null) {
            //Take the Movie List from the ENDPOINT of MovieDB for the first time
            getMoviesBySort(getSortMethod());
        } else {
            // if there is an istance, takes the movies from the parceable array stored in the resources
            Parcelable[] parcelableobject = savedInstanceState.getParcelableArray(getString(R.string.intent_movie));
            //populates the UI from the objects stored and not from the API ENDPOINT
            Movie[] movies = new Movie[parcelableobject.length];
            for (int i = 0; i < parcelableobject.length; i++) {
                movies[i] = (Movie) parcelableobject[i];
            }
            mGridView.setAdapter(new MovieThumbnail(this, movies));
        }
        //FetchFavourites for the firsttime
        FetchFavourites();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu with the two possible sort options
        // Sort by Popularity and Sort by Rating
        mMenu = menu;
        mMenu.add(0, SORTBYPOPULARITY, 1, R.string.sortmostPopular);
        mMenu.add(0, SORTBYRATE, 2, R.string.sorthigestRated);
        mMenu.add(0, SORTBYFAV, 3, R.string.sortFavourite);
        getMenuInflater().inflate(R.menu.menu_main, mMenu);
        return true;
    }

    // the user can update the shared preferences to sort the Gridview Differently
    // bY DEFAULT the app starts with popularity sort

    //For learning goals I've left a GridView for the Stage 1 Sorting and then I've implemented
    // A RecylcerView after for sorting the favourites ( And I've got the differences developing the APP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SORTBYPOPULARITY:
                FetchFavourites();
                mGridView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                updateSharedPreference(SORTBYPOPENDPOINT);
                getMoviesBySort(getSortMethod());
                return true;
            case SORTBYRATE:
                FetchFavourites();
                mGridView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                updateSharedPreference(SORTBYRATEENDPOINT);
                getMoviesBySort(getSortMethod());
                return true;
            case SORTBYFAV:
                FetchFavourites();
                mGridView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                ShowFavorite(getApplicationContext());
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    //MENU AND SHARED PREFERENCES FUNCTIONS SORT BY MOST POPULAR BY DEFAULT
    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(SORTPREFERENCE, SORTBYPOPENDPOINT);
    }

    // Saved the sort option clicked by the user
    private void updateSharedPreference(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SORTPREFERENCE, sortMethod);
        editor.apply();
    }

    //The project rubric asks for a function to send APIs based on 2 sort options
    private void getMoviesBySort(String sortOption) {
        if (isNetworkAvailable()) {
            // Listener for when AsyncTask is ready to update UI
            OnFetchMoviesCompleted taskCompleted = new OnFetchMoviesCompleted() {
                @Override
                public void onUtilsAsyncTaskCompleted(Movie[] movies) {
                    mGridView.setAdapter(new MovieThumbnail(getApplicationContext(), movies));
                    //MovieThumbnail is the adapter for the Movie Images preview
                }
            };
            // Start the UtilAsyncTask to connect to the MovieDB and Manipulate the JSON
            FetchMovies movieUtilTask = new FetchMovies(taskCompleted);
            movieUtilTask.execute(sortOption);
        } else {
            Toast.makeText(this, getString(R.string.NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
        }
    }

    //METHOD To check if there's internet connection
    //TODO: Create methods to update the UI when there is not internet connect (also a spinner while the AsyncTask is loading them)
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // SavedInstance Method to save the movies fetched from the A
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Movie[] movies = new Movie[mGridView.getCount()];
        for (int i = 0; i < mGridView.getCount(); i++) {
            movies[i] = (Movie) mGridView.getItemAtPosition(i);
        }
        outState.putParcelableArray(getString(R.string.intent_movie), movies);
        super.onSaveInstanceState(outState);
    }

    private void ShowFavorite(final Context context) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        // Put the fields of Trailers parsed obect to an ArrayList to inflate the RecylceView
        final List<Movie> favouriteMoviesPoster = new ArrayList<>();
        for (int i = 0; i < mFavouriteMovies.length; i++) {
            favouriteMoviesPoster.add(i, mFavouriteMovies[i]);
        }
        //Adapter
        mMovieAdapter = new MovieAdapter(context, favouriteMoviesPoster, mRecyclerView);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    // I've decided to fetch directly on this Activity the Favourites to work when There's no connection
    // to populate the Favourite View faster, and to communicate to the Details activity that the movie is
    // one of the favoruite movies of the User, I will use the MOVIE Flag IsFavouriteMovies
    private boolean CheckIfFavourite(Movie movie) {
        for (int i = 0; i < mFavouriteMovies.length; i++) {
            if ((movie.movieID).equals(mFavouriteMovies[i].getMovieID())) {
                return true;
            }
        }
        return false;
    }

    public void FetchFavourites() {
        FetchFavouriteMovies favourites = new FetchFavouriteMovies();
        mFavouriteMovies = favourites.doInBackground(getApplicationContext());
    }
}
