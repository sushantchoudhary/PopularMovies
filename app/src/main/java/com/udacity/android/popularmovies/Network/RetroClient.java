package com.udacity.android.popularmovies.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

   private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

   /***
     Get Retrofit Instance
    .addConverterFactory(new NullOnEmptyConverterFactory()) handles null or empty response from network
    */
   private static Retrofit getRetrofitInstance() {
       return new Retrofit.Builder()
               .baseUrl(BASE_URL)
               .addConverterFactory(new NullOnEmptyConverterFactory())
               .addConverterFactory(GsonConverterFactory.create())
               .build();

   }

   public static  ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
   }

}
