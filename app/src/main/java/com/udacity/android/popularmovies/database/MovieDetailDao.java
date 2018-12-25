package com.udacity.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;
import com.udacity.android.popularmovies.model.MovieDetail;
import com.udacity.android.popularmovies.model.MovieRecord;

import java.util.List;

@Dao
public interface MovieDetailDao {

    @Query("SELECT * FROM movie_detail")
    LiveData<List<MovieDetail>> loadAllMoviesDetails();

    @Query("DELETE FROM movie_detail")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieDetail movieDetail);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieDetail movieDetail);

    @Delete
    void deleteMovie(MovieRecord movieRecord);

    @Query("SELECT * FROM movie_detail WHERE movie_id = :id")
    LiveData<MovieDetail> loadMovieById(int id);
}