package com.udacity.android.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import com.udacity.android.popularmovies.database.AppDatabase;
import com.udacity.android.popularmovies.model.MovieReviews;

public class ReviewViewModel extends ViewModel {
    private static final String TAG = ReviewViewModel.class.getSimpleName();
    private LiveData<MovieReviews> movie;

    public ReviewViewModel(AppDatabase appDatabase, int movieId) {
        Log.d(TAG, "Actively retrieving movie detail from database");
        movie = appDatabase.movieReviewsDao().loadReviewById(movieId);
    }

    public LiveData<MovieReviews> getMovieReview() {
        return movie;
    }
}
