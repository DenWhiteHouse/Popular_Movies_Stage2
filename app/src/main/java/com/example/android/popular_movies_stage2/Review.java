package com.example.android.popular_movies_stage2;

/**
 * Created by casab on 01/03/2018.
 */
//Also to take the Reviews as the ENDPOINT is different than the Trailers once I'd would use an Array
public class Review {
    String mAuthur;
    String mReview;

    public void Review() {
    }

    public void setAuthur(String authur) {
        mAuthur = authur;
    }

    public String getAuthur() {
        return mAuthur;
    }

    public void setReview(String review) {
        mReview = review;
    }

    public String getReview() {
        return mReview;
    }
}
