package com.udacity.android.popularmovies.network;

import com.udacity.android.popularmovies.model.MovieDetail;
import com.udacity.android.popularmovies.model.MovieReviews;
import com.udacity.android.popularmovies.model.MovieStore;
import com.udacity.android.popularmovies.model.MovieVideos;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    @GET("popular")
    Call<MovieStore> getPopularMovies(@Query("api_key") String api_key);

    @GET("top_rated")
    Call<MovieStore> getTopRatedMovies(@Query("api_key") String api_key);

    @GET("popular")
    Observable<MovieStore> getRxPopularMovies(@Query("api_key") String api_key);

    @GET("top_rated")
    Observable<MovieStore> getRxTopRatedMovies(@Query("api_key") String api_key);

    @GET("{movie_id}/videos")
    Call<MovieVideos> getMovieVideo(@Path("movie_id") int movie_id, @Query("api_key") String api_key);

    @GET("{movie_id}/reviews")
    Call<MovieReviews> getMovieReview(@Path("movie_id") int movie_id, @Query("api_key") String api_key);


}
