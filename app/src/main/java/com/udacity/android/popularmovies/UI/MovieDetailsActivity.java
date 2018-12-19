package com.udacity.android.popularmovies.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.squareup.picasso.Picasso;
import com.udacity.android.popularmovies.BuildConfig;
import com.udacity.android.popularmovies.Model.MovieRecord;
import com.udacity.android.popularmovies.Model.MovieVideos;
import com.udacity.android.popularmovies.Model.Result;
import com.udacity.android.popularmovies.Network.ApiService;
import com.udacity.android.popularmovies.Network.RetroClient;
import com.udacity.android.popularmovies.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView mMoviePosterIV;
    private TextView mReleaseYear;
    private TextView mRating;
    private TextView mPlotSynopsis;
    private MovieRecord movieRecord;
    private TextView mErrorMessageDisplay;
    private ConstraintLayout mMovieDetailsLayout;
    private ImageView mMoviePosterLargeIV;
    private ToggleButton mFavoriteButton;
    private ProgressBar mLoadingIndicator;
    private YouTubePlayerFragment youtubeFragment;

    private RecyclerView mTrailerRecyclerView;



    private List<Result> mMovieVideos;


    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mMovieDetailsLayout = findViewById(R.id.movie_details_layout);
        mMoviePosterIV = findViewById(R.id.movie_poster);
        mMoviePosterLargeIV = findViewById(R.id.movie_poster_large);
        mReleaseYear = findViewById(R.id.release_year);
        mRating = findViewById(R.id.movie_rating);
        mPlotSynopsis = findViewById(R.id.plot_synopsis);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        // Check for existing state after configuration change and restore the layout
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            movieRecord = savedInstanceState.getParcelable("movieRecord");
            populateUI(movieRecord);
        } else {
            Intent intentFromHome = getIntent();
            if (intentFromHome != null) {
                if (intentFromHome.hasExtra(Intent.EXTRA_TEXT)) {
                    movieRecord = intentFromHome.getParcelableExtra(Intent.EXTRA_TEXT);
                    populateUI(movieRecord);
                }
            }
        }

        loadMovieTrailer(movieRecord);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movieRecord", movieRecord);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving movieId in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movieRecord = savedInstanceState.getParcelable("movieRecord");
        Log.d(TAG, "Restoring movieId from bundle during orientation change");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.movie_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem menuItem = menu.findItem(R.id.action_favorite);

        FrameLayout rootView = (FrameLayout) menuItem.getActionView();
        mFavoriteButton = rootView.findViewById(R.id.button_favorite);

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        int itemId = item.getItemId();
        if (itemId == R.id.action_favorite) {
            mFavoriteButton = findViewById(R.id.button_favorite);
            mFavoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    compoundButton.startAnimation(scaleAnimation);
                }
                // TODO: Save favorite record in DB
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     Populate the UI with the movie record properties
     */
    private void populateUI(MovieRecord movieRecord) {
        if (movieRecord == null) {
            showErrorMessage();
        }
        showMoviewDetailsView();
        String moviePosterUrl = BASE_IMAGE_URL + movieRecord.getPosterPath();
        Context context = mMoviePosterIV.getContext();
        Picasso.with(context)
                .load(moviePosterUrl)
                .placeholder(R.drawable.popular_4k)
                .error(R.drawable.popular_4k)
                .transform(new MaskTransformation(this, R.drawable.rounded_convers_transformation))
                .into(mMoviePosterIV);

        Picasso.with(context)
                .load(moviePosterUrl)
                .placeholder(R.drawable.popular_4k)
                .error(R.drawable.popular_4k)
                .into(mMoviePosterLargeIV);

        mReleaseYear.setText(movieRecord.getReleaseDate().split("-")[0]);
        mRating.setText(Double.toString(movieRecord.getVoteAverage()) + getString(R.string.max_rating));
        mPlotSynopsis.setText(movieRecord.getOverview());

        this.setTitle(movieRecord.getTitle());

    }

    private void showMoviewDetailsView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailsLayout.setVisibility(View.VISIBLE);
    }

    private void loadMovieTrailer(MovieRecord movieRecord) {
        final Call<MovieVideos> movieVideos;

        ApiService apiService = RetroClient.getApiService();
        /**
         * Call movie api to fetch trailers
         */
        movieVideos = apiService.getMovieVideo(movieRecord.getId(), BuildConfig.ApiKey);

        mLoadingIndicator.setVisibility(View.VISIBLE);

        movieVideos.enqueue(new Callback<MovieVideos>() {
            @Override
            public void onResponse(Call<MovieVideos> call, Response<MovieVideos> response) {

                if (response.isSuccessful()) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);

                    if (response.body() != null) {
                        mMovieVideos = response.body().getResults();
                        Result movieTrailer = mMovieVideos.get(0);
                       final String youtubeKey = movieTrailer.getKey();
                        //TODO Invoke media player with the youtube url including youtubeKey

                        youtubeFragment.initialize("AIzaSyDfGzKHA-q4bldzopjnBdV7g2EG1lQIH5Q",
                                new YouTubePlayer.OnInitializedListener() {
                                    @Override
                                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                        YouTubePlayer player, boolean wasRestored) {

                                        if (null == player) return;
                                        if (!wasRestored) {
                                            player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                                            player.cueVideo(youtubeKey);
                                        }

                                    }

                                    @Override
                                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                        YouTubeInitializationResult youTubeInitializationResult) {
                                        if (youTubeInitializationResult.isUserRecoverableError()) {
                                            youTubeInitializationResult.getErrorDialog(MovieDetailsActivity.this, RECOVERY_DIALOG_REQUEST).show();
                                        } else {
                                            String errorMessage = String.format(
                                                    "There was an error initializing the player (%1$s)",
                                                    youTubeInitializationResult.toString());
                                            Toast.makeText(MovieDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                    } else {
                        throw new HttpException(response);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieVideos> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mMovieDetailsLayout.setVisibility(View.INVISIBLE);
                showErrorMessage();
            }
        });
    }


    private void showErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
    }

}
