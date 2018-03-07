package com.example.android.popular_movies_stage2;


interface OnFetchMoviesCompleted {
    void onUtilsAsyncTaskCompleted(Movie[] movies);
}

interface OnFetchTrailersCompleted {
    void onUtilsAsyncTaskCompleted(Trailer[] trailers);
}

interface OnFetchReviewsCompleted {
    void onUtilsAsyncTaskCompleted(Review[] revies);
}

interface OnFetchFavouriteCompleted {
    void onUtilsAsyncTaskCompleted(Movie[] movies);
}
