package com.example.android.popular_movies_stage2;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Created by casab on 23/02/2018.
 */

public class FetchMovies extends AsyncTask<String, Void, Movie[]> {
    // LOG message
    private static final String LOG_TAG = FetchMovies.class.getSimpleName();
    //STATICS for the API call
    private String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private String API_KEY_BASE = "api_key";
    private String API_KEY = "";
    //PATHs of the JSON to read
    private String ORIGINAL_TITLE = "original_title";
    private String IMAGE_PATH = "poster_path";
    private String SYNOPSIS = "overview";
    private String RATING = "vote_average";
    private String RELEASE_DATE = "release_date";
    private String MOVIEID = "id";
    //Listener and API Request Helpers
    private OnFetchMoviesCompleted mListener;
    private String mAPIkey = API_KEY;
    private String JSONresults="results";

    public FetchMovies(OnFetchMoviesCompleted listener) {
        super();
        mListener = listener;
    }

    //Fetching the movies
    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = null;

        try {
            URL url = createUrl(params);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream == null) {
                return null;}
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            if (builder.length() == 0) {
                return null;
            }

            jsonResponse = builder.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making http request.", e);
        }
        return extractResultsFromJson(jsonResponse);
    }

    //Method to create the URL for the Request from the URI paths
    private URL createUrl(String[] parameters) throws MalformedURLException {
        String base= TMDB_BASE_URL + parameters[0];
        Uri builtUri = Uri.parse(base).buildUpon().appendQueryParameter(API_KEY_BASE, mAPIkey).build();
        URL url = new URL(builtUri.toString());
        return url;
    }

    //Method to extract the results and create a News Object
    private Movie[] extractResultsFromJson(String movieJSON) {
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }
        try {
            JSONObject baseJsonResponse = new JSONObject(movieJSON);
            JSONArray results = baseJsonResponse.getJSONArray(JSONresults);
            // Array of Movies from the JSON results
            Movie[] movies = new Movie[results.length()];
            int j = results.length(); // support variable for debug JSON parsing

            for (int i = 0; i < results.length(); i++) {
                // initiliaze a new index array
                movies[i] = new Movie();
                ;
                //Taking results from the JSON object
                JSONObject jsonObject = results.getJSONObject(i);
                // Store fields of the Object in the Movie Array
                movies[i].setImagePath(jsonObject.getString(IMAGE_PATH));
                movies[i].setOriginalTitle(jsonObject.getString(ORIGINAL_TITLE));
                movies[i].setSynopsis(jsonObject.getString(SYNOPSIS));
                movies[i].setRating(jsonObject.getString(RATING));
                movies[i].setMovieReleaseDate(jsonObject.getString(RELEASE_DATE));
                movies[i].setMovieID(jsonObject.getString(MOVIEID));
            }
            return movies;
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the Movie JSON results", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        mListener.onUtilsAsyncTaskCompleted(movies);
    }

}