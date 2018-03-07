package com.example.android.popular_movies_stage2;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.popular_movies_stage2.MoviesDBContract.MovieEntry;
import com.example.android.popular_movies_stage2.MoviesDBContract.ReviewEntry;
import com.example.android.popular_movies_stage2.MoviesDBContract.TrailerEntry;


/**
 * Created by casab on 04/03/2018.
 */

// Helper Structure as I've done for the Android Basics Nanodegree FruitShop Project

public class MovieContentProvider extends ContentProvider {
    public static final String LOG_TAG = MovieContentProvider.class.getSimpleName();
    //URI MATCHER for the tables
    private static final int MOVIETABLE = 100;
    private static final int MOVIE = 101;
    private static final int TRAILERTABLE = 200;
    private static final int TRAILER = 201;
    private static final int REVIEWTABLE = 300;
    private static final int REVIEW = 301;
    //URIMATCHER
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer of Matcher
    static {
        sUriMatcher.addURI(MoviesDBContract.CONTENT_AUTHORITY, MoviesDBContract.PATH_MOVIEDB, MOVIETABLE);
        sUriMatcher.addURI(MoviesDBContract.CONTENT_AUTHORITY, MoviesDBContract.PATH_MOVIEDB + "/#", MOVIE);
        //TODO: Add here Statics for Trailers and Reviews

    }

    public MovieDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        // On Updated code mDBHlper is set directly accessing the viriable
        // mDBHelper = new MovieDBHelper(getContext());
        return true;
    }


    // Understands if a table or a single item
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIETABLE:
                return MovieEntry.CONTENT_LIST_TYPE;
            case MOVIE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILERTABLE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEWTABLE:
                return MovieEntry.CONTENT_LIST_TYPE;
            case REVIEW:
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIETABLE:
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIE:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRAILERTABLE:
                cursor = database.query(TrailerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRAILER:
                selection = TrailerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REVIEWTABLE:
                cursor = database.query(ReviewEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REVIEW:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ReviewEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    //Insertert into DB Method
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIETABLE:
                return insertMovie(uri, contentValues);
            case REVIEWTABLE:
                return insertReview(uri, contentValues);
            case TRAILERTABLE:
                return insertTrailer(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * *CRUD Functionalities
     **/

    //Insert Cases
    private Uri insertMovie(Uri uri, ContentValues values) {
        mDBHelper = new MovieDBHelper(getContext());
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Insert the moview into the DB
        long id = database.insert(MovieEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify the listeners
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertTrailer(Uri uri, ContentValues values) {
        String TrailerKey = values.getAsString(TrailerEntry.COLUMN_TRAILER_KEY);

        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Insert the moview into the DB
        long id = database.insert(TrailerEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify the listeners
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertReview(Uri uri, ContentValues values) {
        String author = values.getAsString(ReviewEntry.COLUMN_AUTHOR);
        String content = values.getAsString(ReviewEntry.COLUMN_CONTENT);

        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Insert the moview into the DB
        long id = database.insert(ReviewEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify the listeners
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIETABLE:
                return updateMovie(uri, contentValues, selection, selectionArgs);
            case MOVIE:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMovie(uri, contentValues, selection, selectionArgs);
            case TRAILERTABLE:
                return updateTrailer(uri, contentValues, selection, selectionArgs);
            case TRAILER:
                selection = TrailerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTrailer(uri, contentValues, selection, selectionArgs);
            case REVIEWTABLE:
                return updateReview(uri, contentValues, selection, selectionArgs);
            case REVIEW:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateReview(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateTrailer(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int rowsUpdated = database.update(TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateReview(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int rowsUpdated = database.update(ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIETABLE:
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERTABLE:
                rowsDeleted = database.delete(TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                selection = TrailerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWTABLE:
                rowsDeleted = database.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    public int deleteSingleMovie(Uri uri, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        String selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
        rowsDeleted = database.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
        // Return the number of rows deleted
        return rowsDeleted;
    }
}
