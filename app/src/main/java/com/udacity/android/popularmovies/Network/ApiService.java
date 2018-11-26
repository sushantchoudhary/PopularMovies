package com.udacity.android.popularmovies.Network;

import com.udacity.android.popularmovies.Model.MovieStore;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("popular")
    Call<MovieStore> getPopularMovies(@Query("api_key") String api_key);

    @GET("top_rated")
    Call<MovieStore> getTopRatedMovies(@Query("api_key") String api_key);

}
