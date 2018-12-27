package com.udacity.android.popularmovies.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import com.udacity.android.popularmovies.database.AppDatabase;

public class ReviewViewModelFactory extends   ViewModelProvider.NewInstanceFactory{

    private final AppDatabase mDB;
    private final int movieId;

    public ReviewViewModelFactory(AppDatabase mDB, int movieId) {
        this.mDB = mDB;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ReviewViewModel(mDB, movieId);
    }
}
