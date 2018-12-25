package com.udacity.android.popularmovies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favmovies")
public class FavMovies {

    @PrimaryKey
    @SerializedName("movie_id")
    @Expose
    private Integer movieId;

    @SerializedName("favorite")
    @Expose
    private Boolean favorite;

    public FavMovies(Integer movieId, Boolean favorite) {
        this.movieId = movieId;
        this.favorite = favorite;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
