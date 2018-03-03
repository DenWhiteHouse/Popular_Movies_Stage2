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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {//SORT METHODS STATIC ID KEYS
    private final int SORTBYPOPULARITY = 1;
    private final int SORTBYRATE = 2;
    //KEYVariable for Sort Preference SHARED PREFERENCE
    private String SORTPREFERENCE = "/popular";
    //SORT METHODS ENDPOINTS
    private String SORTBYPOPENDPOINT = "/popular";
    private String SORTBYRATEENDPOINT = "/top_rated";

    //When a thumbnail is clicked open the details activity
    private final GridView.OnItemClickListener movieThumbClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //take the movie to pass from the Array based on the position
            Movie movie = (Movie) parent.getItemAtPosition(position);
            //Sends an intent to the Deatail Activity with the selected movie object as a Parceable Extra
            Intent intent = new Intent(getApplicationContext(), MovieDetails.class);
            intent.putExtra(getResources().getString(R.string.intent_movie), movie);
            startActivity(intent);
        }
    };
    //The main activity should show a GridView and an Menu to set the order
    // As the options to sort the Grid are very few and don't determinate deep APP changes I'd go for SharedPreferences
    private GridView mGridView;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set the view and the listener to the movie image ( if clicked opens the details activity)
        mGridView = findViewById(R.id.GridView);
        mGridView.setOnItemClickListener(movieThumbClickListener);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu with the two possible sort options
        // Sort by Popularity and Sort by Rating
        mMenu = menu;
        mMenu.add(0, SORTBYPOPULARITY, 1, R.string.sortmostPopular);
        mMenu.add(0, SORTBYRATE, 2, R.string.sorthigestRated);
        getMenuInflater().inflate(R.menu.menu_main, mMenu);
        return true;
    }

    // the user can update the shared preferences to sort the Gridview Differently
    // bY DEFAULT the app starts with popularity sort
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SORTBYPOPULARITY:
                updateSharedPreference(SORTBYPOPENDPOINT);
                getMoviesBySort(getSortMethod());
                return true;
            case SORTBYRATE:
                updateSharedPreference(SORTBYRATEENDPOINT);
                getMoviesBySort(getSortMethod());
                return true;
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
}
