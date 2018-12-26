package com.udacity.android.popularmovies.ui;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;
import com.udacity.android.popularmovies.BuildConfig;
import com.udacity.android.popularmovies.R;
import com.udacity.android.popularmovies.database.AppDatabase;
import com.udacity.android.popularmovies.model.*;
import com.udacity.android.popularmovies.network.ApiService;
import com.udacity.android.popularmovies.network.RetroClient;
import com.udacity.android.popularmovies.utils.AppExecutors;
import com.udacity.android.popularmovies.viewmodel.MovieDetailViewModelFactory;
import com.udacity.android.popularmovies.viewmodel.MovieDetailsViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static AppDatabase mDB;
    public YouTubePlayer youTubePlayer;
    public boolean isYouTubePlayerFullScreen;
    private ImageView mMoviePosterIV;
    private TextView mReleaseYear;
    private TextView mRating;
    private TextView mPlotSynopsis;
    private TextView mReviews;
    private MovieRecord movieRecord;
    private Menu mFavMenu;
    private TextView mErrorMessageDisplay;
    private ConstraintLayout mMovieDetailsLayout;
    private ImageView mMoviePosterLargeIV;
    private ToggleButton mFavoriteButton;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mTrailerRecyclerView;
    private TrailerGalleryAdapter trailerAdapter;
    private List<Result> mMovieVideos;
    private MovieReviews mMovieReviews;


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
        mReviews = findViewById(R.id.reviewText);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mTrailerRecyclerView = findViewById(R.id.recyclerview_trailer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mTrailerRecyclerView.setLayoutManager(linearLayoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);

        mDB = AppDatabase.getsInstance(getApplicationContext());

        // Check for existing state after configuration change and restore the layout
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            movieRecord = savedInstanceState.getParcelable("movieRecord");
            setupMovieDetailFromViewModel(movieRecord);
//            populateUI(movieRecord);
        } else {
            Intent intentFromHome = getIntent();
            if (intentFromHome != null) {
                if (intentFromHome.hasExtra(Intent.EXTRA_TEXT)) {
                    movieRecord = intentFromHome.getParcelableExtra(Intent.EXTRA_TEXT);
//                    setupMovieDetailFromViewModel(movieRecord);
                    loadMovieReviewsInDB();
                    populateUI(movieRecord);
                }
            }
        }
        /**
         * Loading this view at the end to allow Youtube player render
         */
        loadMovieTrailer(movieRecord);
    }

    private void setupMovieDetailFromViewModel(MovieRecord record) {
        MovieDetailViewModelFactory factory = new MovieDetailViewModelFactory(mDB, record.getId());
        final MovieDetailsViewModel viewModel = ViewModelProviders.of(this, factory).get(MovieDetailsViewModel.class);
        viewModel.getMovie().observe(this, new Observer<MovieRecord>() {
            @Override
            public void onChanged(@Nullable MovieRecord movieRecord) {
                viewModel.getMovie().removeObserver(this);
                Log.d(TAG, "Receiving database update from LiveData");
                populateUI(movieRecord);
            }
        });
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
        mFavMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem menuItem = menu.findItem(R.id.action_favorite);
        FrameLayout rootView = (FrameLayout) menuItem.getActionView();
        mFavoriteButton = rootView.findViewById(R.id.button_favorite);
        setFavStatus(mFavoriteButton);
        mFavoriteButton.setOnClickListener(view -> onOptionsItemSelected(menuItem));

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
            //TODO: This check could be simplified
            if (mFavoriteButton.isPressed() && mFavoriteButton.isChecked()) {
                Log.d(TAG, "Favorite selected after first transition");
                mFavoriteButton.setChecked(true);
            }

            mFavoriteButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(scaleAnimation);
                if (!isChecked) {
                    Log.d(TAG, "Favorite unselected");
                    mFavoriteButton.setChecked(false);
                } else {
                    Log.d(TAG, "Favorite selected");
                    mFavoriteButton.setChecked(true);
                }
            });
            final FavMovies favMovie = new FavMovies(movieRecord.getId(), mFavoriteButton.isChecked());
            //TODO : Create builder
            final MovieSnapshot movieSnapshot = new MovieSnapshot(
                    movieRecord.getVoteCount(),
                    movieRecord.getId(),
                    movieRecord.getVideo(),
                    movieRecord.getVoteAverage(),
                    movieRecord.getTitle(),
                    movieRecord.getPopularity(),
                    movieRecord.getPosterPath(),
                    movieRecord.getOriginalLanguage(),
                    movieRecord.getOriginalTitle(),
                    movieRecord.getGenreIds(),
                    movieRecord.getBackdropPath(),
                    movieRecord.getAdult(),
                    movieRecord.getOverview(),
                    movieRecord.getReleaseDate());

            AppExecutors.getInstance().diskIO().execute(() -> {
                if (mFavoriteButton.isChecked()) {
                    mDB.runInTransaction(() -> {
                        mDB.favDao().insertFavMovie(favMovie);
                        // FIXME : Cant insert data into movie_snapshot using this query
//                            mDB.query("INSERT INTO movie_snapshot SELECT * FROM movie WHERE movie_id = ?", new Object[]{favMovie.getMovieId()});
                        mDB.movieSnapshotDao().insertMovieSnapshot(movieSnapshot);
                    });
                } else {
                    mDB.favDao().deleteFavMovie(favMovie);
                    mDB.movieSnapshotDao().deleteMovieSnapshot(movieSnapshot);
                }
            });
            return true;
        }

        /***
         * Handle back navigation since this activity is launched from both Main and Fav view
         */
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (youTubePlayer != null && isYouTubePlayerFullScreen) {
            youTubePlayer.setFullscreen(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        setupMovieDetailFromViewModel(movieRecord);

    }

    private void setFavStatus(ToggleButton button) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            final LiveData<FavMovies> favData = mDB.favDao().loadFavMovieById(movieRecord.getId());
            favData.observe(MovieDetailsActivity.this, favMovies -> runOnUiThread(() -> {
                if (favMovies != null) {
                    //TODO : Need not init button again
//                                  mFavoriteButton = findViewById(R.id.button_favorite);
                    button.setChecked(favMovies.getFavorite());
                }
            }));
        });
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
                        //TODO Invoke media player with the youtube url including youtubeKey
                        trailerAdapter = new TrailerGalleryAdapter(MovieDetailsActivity.this, mMovieVideos);
                        mTrailerRecyclerView.setAdapter(trailerAdapter);
                        trailerAdapter.notifyDataSetChanged();


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

    private void loadMovieReviewsInDB() {
        final Call<MovieReviews> movieReviewsCall;

        ApiService apiService = RetroClient.getApiService();
        /**
         * Call movie api to fetch reviews
         */
        movieReviewsCall = apiService.getMovieReview(movieRecord.getId(), BuildConfig.ApiKey);

        mLoadingIndicator.setVisibility(View.VISIBLE);

        movieReviewsCall.enqueue(new Callback<MovieReviews>() {
            @Override
            public void onResponse(Call<MovieReviews> call, Response<MovieReviews> response) {

                if (response.isSuccessful()) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);

                    if (response.body() != null) {
                        mMovieReviews = response.body();
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            mDB.movieReviewsDao().insertMovieReview(mMovieReviews);
                        });
                        /**
                         * Updating UI with reviews from DB
                         */
                        mDB.movieReviewsDao().loadReviewById(movieRecord.getId()).observe(MovieDetailsActivity.this, new Observer<MovieReviews>() {
                            @Override
                            public void onChanged(@Nullable MovieReviews movieReviews) {
                                runOnUiThread(() -> {
                                    StringBuilder sbl = new StringBuilder();
                                    //TODO Use ConstraintSet to add text views dynamically
                                    for(ReviewResult result : movieReviews.getResults()) {
                                        sbl.append(result.getContent()+ "\n" + "\n");
                                    }
                                    mReviews.setText(sbl.toString());

                                });
                            }
                        });
                    } else {
                        throw new HttpException(response);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieReviews> call, Throwable t) {
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
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> finish()).create().show();
    }

}
