package com.udacity.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;
import com.udacity.android.popularmovies.model.FavMovies;

import java.util.List;

@Dao
public abstract class FavDao {
    @Query("SELECT * FROM favmovies ORDER BY movieId")
    public abstract LiveData<List<FavMovies>> loadAllFavoriteMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertFavMovie(FavMovies favMovies);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateFavMovie(FavMovies favMovies);

    @Delete
    public abstract void deleteFavMovie(FavMovies favMovies);

    @Query("SELECT * FROM favmovies WHERE movieId = :id")
    public abstract LiveData<FavMovies> loadFavMovieById(int id);

//    @Query("INSERT INTO movie_snapshot SELECT * FROM movie where movie_id= :id")
//    public abstract void copyFavRecordsToSnapshot(int id);

//    @Transaction
//    public void insertAndUpdateFavTransaction(FavMovies favMovies, int movieId) {
//        // This method runs in a single transaction.
//        insertFavMovie(favMovies);
//        copyFavRecordsToSnapshot(movieId);
//    }

}
