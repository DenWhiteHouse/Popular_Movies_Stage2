package com.example.android.popular_movies_stage2;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

/**
 * Created by casab on 06/03/2018.
 */

public class FetchFavouriteMovies extends AsyncTask<Context, Void, Movie[]> {
    MovieContentProvider mContentProvider;
    Cursor mCursor;
    Movie[] mMovies;
    Context mContext;

    public FetchFavouriteMovies() {
        super();
    }

    //Fetching the mfavorites from the Local DB
    @Override
    protected Movie[] doInBackground(Context... params) {
        mContentProvider = new MovieContentProvider();
        mContext = params[0];
        mContentProvider.mDBHelper = new MovieDBHelper(mContext);

        String[] projection = {
                MoviesDBContract.MovieEntry.COLUMN_MOVIE_ID,
                MoviesDBContract.MovieEntry.COLUMN_POSTER_PATH,
                MoviesDBContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                MoviesDBContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                MoviesDBContract.MovieEntry.COLUMN_RELEASE_DATE,
                MoviesDBContract.MovieEntry.COLUMN_OVERVIEW};
        // Putting the data from the DB into a Movie[] Array

        mCursor = mContentProvider.query(MoviesDBContract.MovieEntry.CONTENT_URI, projection, null, null, null);
        mMovies = new Movie[mCursor.getCount()];
        int indexMovie = 0;

        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            mMovies[indexMovie] = new Movie();
            mMovies[indexMovie].setMovieID(mCursor.getString(mCursor.getColumnIndex(MoviesDBContract.MovieEntry.COLUMN_MOVIE_ID)));
            mMovies[indexMovie].setImagePath(mCursor.getString(mCursor.getColumnIndex(MoviesDBContract.MovieEntry.COLUMN_POSTER_PATH)));
            mMovies[indexMovie].setOriginalTitle(mCursor.getString(mCursor.getColumnIndex(MoviesDBContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
            mMovies[indexMovie].setRating(mCursor.getString(mCursor.getColumnIndex(MoviesDBContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            mMovies[indexMovie].setMovieReleaseDate(mCursor.getString(mCursor.getColumnIndex(MoviesDBContract.MovieEntry.COLUMN_RELEASE_DATE)));
            mMovies[indexMovie].setSynopsis(mCursor.getString(mCursor.getColumnIndex(MoviesDBContract.MovieEntry.COLUMN_OVERVIEW)));
            indexMovie++;
        }
        return mMovies;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
    }
}

