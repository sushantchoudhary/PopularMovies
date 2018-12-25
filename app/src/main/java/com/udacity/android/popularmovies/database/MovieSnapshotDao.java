package com.udacity.android.popularmovies.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;
import com.udacity.android.popularmovies.model.MovieSnapshot;

import java.util.List;

@Dao
public interface MovieSnapshotDao {

    @Query("SELECT * FROM movie_snapshot")
    LiveData<List<MovieSnapshot>> loadAllMovies();

    @Query("DELETE FROM movie_snapshot")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMovieSnapshot(MovieSnapshot movieSnapshot);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieSnapshot movieSnapshot);

    @Delete
    void deleteMovieSnapshot(MovieSnapshot movieSnapshot);

    @Query("SELECT * FROM movie_snapshot WHERE movie_id = :id")
    LiveData<MovieSnapshot> loadMovieById(int id);
}

