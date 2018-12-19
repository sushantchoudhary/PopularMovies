package com.udacity.android.popularmovies.Network;

import com.udacity.android.popularmovies.Model.MovieStore;
import com.udacity.android.popularmovies.Model.MovieVideos;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("popular")
    Call<MovieStore> getPopularMovies(@Query("api_key") String api_key);

    @GET("top_rated")
    Call<MovieStore> getTopRatedMovies(@Query("api_key") String api_key);

    @GET("{movie_id}/videos")
    Call<MovieVideos> getMovieVideo(@Path ("movie_id") int movie_id, @Query("api_key") String api_key);

}
