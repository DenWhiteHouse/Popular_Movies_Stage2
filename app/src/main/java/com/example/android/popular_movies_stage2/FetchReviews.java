package com.example.android.popular_movies_stage2;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by casab on 02/03/2018.
 */

public class FetchReviews extends AsyncTask<String, Void, Review[]> {
    // LOG message
    private static final String LOG_TAG = FetchReviews.class.getSimpleName();
    //ENDPOINTs KEYValues are here so they can be easily be updated in case the programs would fetch trailers
    //and reviews from external resources
    private String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private String TRAILER_ENDPOINT = "/reviews";
    private String API_KEY_BASE = "api_key";
    private String API_KEY = "";
    //JSON Exploration
    private String JSONAuthur = "author";
    private String JSONReview = "content";
    private String JSONresults = "results";
    //Utils
    private OnFetchReviewsCompleted mListener;

    public FetchReviews(OnFetchReviewsCompleted listener) {
        super();
        mListener = listener;
    }

    @Override
    protected Review[] doInBackground(String... params) {
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
                return null;
            }
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
        String base = TMDB_BASE_URL + parameters[0] + TRAILER_ENDPOINT;
        Uri builtUri = Uri.parse(base).buildUpon().appendQueryParameter(API_KEY_BASE, API_KEY).build();
        URL url = new URL(builtUri.toString());
        return url;
    }

    //Method to extract the results and create a News Object
    private Review[] extractResultsFromJson(String reviewJSON) {
        if (TextUtils.isEmpty(reviewJSON)) {
            return null;
        }
        try {
            JSONObject baseJsonResponse = new JSONObject(reviewJSON);
            JSONArray results = baseJsonResponse.getJSONArray(JSONresults);
            // Array of Movies from the JSON results
            Review[] reviews = new Review[results.length()];
            int j = results.length(); // support variable for debug JSON parsing

            for (int i = 0; i < results.length(); i++) {
                // initiliaze a new index array
                reviews[i] = new Review();
                ;
                //Taking results from the JSON object
                JSONObject jsonObject = results.getJSONObject(i);
                // Store fields of the Object in the Movie Array
                reviews[i].setAuthur(jsonObject.getString(JSONAuthur));
                reviews[i].setReview(jsonObject.getString(JSONReview));

            }
            return reviews;
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the Movie JSON results", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Review[] reviews) {
        super.onPostExecute(reviews);
        mListener.onUtilsAsyncTaskCompleted(reviews);
    }

}