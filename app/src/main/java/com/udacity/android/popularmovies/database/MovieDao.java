package com.udacity.android.popularmovies.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;
import com.udacity.android.popularmovies.model.MovieRecord;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    LiveData<List<MovieRecord>> loadAllMovies();

    @Query("DELETE FROM movie")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieRecord movieRecord);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertMovieList(List<MovieRecord> movieRecords);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieRecord movieRecord);

    @Delete
    void deleteMovie(MovieRecord movieRecord);

    @Query("SELECT * FROM movie WHERE movie_id = :id")
    LiveData<MovieRecord> loadMovieById(int id);
}
