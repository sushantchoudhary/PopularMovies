package com.udacity.android.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import com.udacity.android.popularmovies.database.AppDatabase;
import com.udacity.android.popularmovies.model.MovieRecord;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();


    private LiveData<List<MovieRecord>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Actively retriveing task from database");
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();

    }

    public LiveData<List<MovieRecord>> getMovies() {
        return movies;
    }
}
