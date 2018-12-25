package com.udacity.android.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import com.udacity.android.popularmovies.database.AppDatabase;
import com.udacity.android.popularmovies.model.MovieSnapshot;

import java.util.List;

public class MovieSnapshotViewModel extends AndroidViewModel {

    private static final String TAG = MovieSnapshotViewModel.class.getSimpleName();


    private LiveData<List<MovieSnapshot>> movies;

    public MovieSnapshotViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Actively retriveing task from database");
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        movies = database.movieSnapshotDao().loadAllMovies();

    }

    public LiveData<List<MovieSnapshot>> getMovies() {
        return movies;
    }
}

