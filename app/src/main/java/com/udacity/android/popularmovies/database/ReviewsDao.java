package com.udacity.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;
import com.udacity.android.popularmovies.model.MovieReviews;


@Dao
public interface ReviewsDao {

    @Query("SELECT * FROM movie_review")
    LiveData<MovieReviews> loadAllReviews();

    @Query("DELETE FROM movie_review")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieReview(MovieReviews movieReviews);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovieReview(MovieReviews movieReviews);

    @Delete
    void deleteMovieReview(MovieReviews movieReviews);

    @Query("SELECT * FROM movie_review WHERE id = :id")
    LiveData<MovieReviews> loadReviewById(int id);
}