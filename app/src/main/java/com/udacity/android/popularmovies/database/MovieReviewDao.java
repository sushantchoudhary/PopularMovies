package com.udacity.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;
import com.udacity.android.popularmovies.model.MovieReviews;
import com.udacity.android.popularmovies.model.ReviewResult;

import java.util.List;

@Dao
public interface MovieReviewDao {
    @Query("SELECT * FROM review")
    LiveData<ReviewResult> loadAllReviews();

    @Query("DELETE FROM review")
    void deleteAllRows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieReview(ReviewResult reviewResult);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovieReview(ReviewResult reviewResult);

    @Delete
    void deleteMovieReview(ReviewResult reviewResult);

    @Query("SELECT * FROM review WHERE id = :id")
    LiveData<List<ReviewResult>> loadReviewById(int id);
}
