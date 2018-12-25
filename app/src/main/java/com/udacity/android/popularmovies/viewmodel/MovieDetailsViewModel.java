package com.udacity.android.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import com.udacity.android.popularmovies.database.AppDatabase;
import com.udacity.android.popularmovies.model.MovieRecord;

public class MovieDetailsViewModel extends ViewModel {

    private static final String TAG = MovieDetailsViewModel.class.getSimpleName();
    private LiveData<MovieRecord> movie;

    public MovieDetailsViewModel(AppDatabase appDatabase, int movieId) {
        Log.d(TAG, "Actively retrieving movie detail from database");
        movie = appDatabase.movieDao().loadMovieById(movieId);
    }

    public LiveData<MovieRecord> getMovie() {
        return movie;
    }
}
