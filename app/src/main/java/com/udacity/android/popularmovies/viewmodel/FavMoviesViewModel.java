package com.udacity.android.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.udacity.android.popularmovies.database.AppDatabase;
import com.udacity.android.popularmovies.model.FavMovies;

import java.util.List;

public class FavMoviesViewModel extends AndroidViewModel {

    private LiveData<List<FavMovies>> favMovies;

    public FavMoviesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        favMovies = database.favDao().loadAllFavoriteMovies();
    }

    public LiveData<List<FavMovies>> getFavMovies() {
        return favMovies;
    }

}
