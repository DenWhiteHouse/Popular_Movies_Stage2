package com.example.android.popular_movies_stage2;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by casab on 04/03/2018.
 */

public final class MoviesDBContract {
    // Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.android.popular_movies_stage2";
    // Base URI to contact the Content Provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible Path for the Content Provider
    public static final String PATH_MOVIEDB = "moviedb";

    // To prevent someone from accidentally instantiating the contract class, emtpy constructor
    private MoviesDBContract() {
    }

    public static final class MovieEntry implements BaseColumns {
        //Setting of MovieDB-Content Provider Fielss
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIEDB);
        // From Android Basics Udacity FruitsShops

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Movies.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIEDB;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Movie.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIEDB;

        // DATABASE SETTINGS
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";

    }

    //TODO: Improve and customize the project by storing also trailers and reviws

    //Table for Trailers
    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_KEY = "trailer_key";

    }

    //Table for Reviews
    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

    }

}

