package com.udacity.android.popularmovies.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.udacity.android.popularmovies.BuildConfig;
import com.udacity.android.popularmovies.Model.MovieDetail;
import com.udacity.android.popularmovies.Network.ApiService;
import com.udacity.android.popularmovies.Network.RetroClient;
import com.udacity.android.popularmovies.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView mMoviePosterIV;
    private TextView mReleaseYear;
    private TextView mDuration;
    private TextView mRating;
    private TextView mPlotSynopsis;
    private Integer movieId;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private ConstraintLayout mMovieDetailsLayout;
    private TextView mMovieTitle;

    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mMovieDetailsLayout = findViewById(R.id.movie_details_layout);

        mMoviePosterIV = findViewById(R.id.movie_poster);
        mReleaseYear = findViewById(R.id.release_year);
        mDuration = findViewById(R.id.movie_duration);
        mRating = findViewById(R.id.movie_rating);
        mPlotSynopsis = findViewById(R.id.plot_synopsis);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mMovieTitle = findViewById(R.id.movie_title);

        Intent intentFromHome = getIntent();

        if (intentFromHome != null) {
            if (intentFromHome.hasExtra(Intent.EXTRA_TEXT)) {
                movieId = intentFromHome.getIntExtra(Intent.EXTRA_TEXT, 0);
                loadMovieDetails(movieId);
            }
        }

        this.setTitle("Movie Detail");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("movieId", movieId);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving movieId in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movieId = savedInstanceState.getInt("movieId");
        Log.d(TAG, "Restoring movieId from bundle during orientation change");

    }

    /***
     Retrofit call to fetch movie details and populate the UI
     */
    private void loadMovieDetails(Integer movieId) {
        Call<MovieDetail> callmoviedb;
        showMoviewDetailsView();
        ApiService apiService = RetroClient.getApiService();
        callmoviedb = apiService.getMovieDetails(movieId, BuildConfig.ApiKey);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        callmoviedb.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {

                if (response.isSuccessful()) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);

                    if (response.body() != null) {
                        String mMoviePosterPath = response.body().getPosterPath();
                        String title = response.body().getOriginalTitle();

                        String releaseDate = response.body().getReleaseDate().split("-")[0];

                        Integer duration = response.body().getRuntime();
                        Double rating = response.body().getVoteAverage();
                        String synopsis = response.body().getOverview();
                        String moviePosterUrl = BASE_IMAGE_URL + mMoviePosterPath;
                        Context context = mMoviePosterIV.getContext();
                        Picasso.with(context)
                                .load(moviePosterUrl)
                                .placeholder(R.drawable.popular_4k)
                                .error(R.drawable.popular_4k)
                                .into(mMoviePosterIV);

                        mMovieTitle.setText(title);
                        mReleaseYear.setText(releaseDate);
                        mDuration.setText(String.valueOf(duration) + getString(R.string.movie_duration));
                        mRating.setText(Double.toString(rating) + getString(R.string.max_rating));
                        mPlotSynopsis.setText(synopsis);

                    } else {
                        throw new HttpException(response);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mMovieDetailsLayout.setVisibility(View.INVISIBLE);
                showErrorMessage();
            }
        });
    }


    private void showMoviewDetailsView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailsLayout.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle("W00t, something failed")
                .setMessage("Please check network connection and try again")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
    }

}
