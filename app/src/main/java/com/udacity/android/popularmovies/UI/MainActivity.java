package com.udacity.android.popularmovies.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.udacity.android.popularmovies.BuildConfig;
import com.udacity.android.popularmovies.Model.MovieRecord;
import com.udacity.android.popularmovies.Model.MovieStore;
import com.udacity.android.popularmovies.Network.ApiService;
import com.udacity.android.popularmovies.Network.RetroClient;
import com.udacity.android.popularmovies.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.PopularMoviesAdapterOnClickHandler {

    private RecyclerView mPopMovieLayout;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private ArrayList<MovieRecord> mMovieList;
    private PopularMoviesAdapter popularMoviesAdapter;
    private SortType preferredSort;


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final Integer SPAN_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPopMovieLayout = findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT, GridLayoutManager.VERTICAL, false);

        mPopMovieLayout.setLayoutManager(gridLayoutManager);

        mPopMovieLayout.setHasFixedSize(true);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            preferredSort = (SortType) savedInstanceState.getSerializable("preferredSort");
            loadPopularMovies(preferredSort);
        } else {
            loadPopularMovies(SortType.POPULAR);
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("preferredSort", preferredSort);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving preferredSort in bundle during orientation change");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        preferredSort = (SortType) savedInstanceState.getSerializable("preferredSort");
        Log.d(TAG, "Restoring preferredSort from bundle during orientation change");

    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param movieRecord Id of movie record in the database
     */
    @Override
    public void onClick(MovieRecord movieRecord) {
        Context context = this;
        Class destinationClass = MovieDetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movieRecord);
        startActivity(intentToStartDetailActivity);
    }

    /***
     * This method is overridden by MainActivity class to populate sorting menu options
     * @param menu Menu layout to be inflated
     * @return boolean
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.popular_movie, menu);
        return true;
    }


    /***
     * This method is overridden by MainActivity class to populate sorting menu option
     * @param item Menu item displayed in app bar
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        popularMoviesAdapter.setMoviesData(null);
        if (id == R.id.action_sortbypopularity) {
            preferredSort = SortType.POPULAR;
        } else {
            preferredSort = SortType.TOP_RATED;
        }
        loadPopularMovies(preferredSort);
        return true;
    }

    /***
     Retrofit call to fetch movie posters and populate the UI
     */
    private void loadPopularMovies(SortType sortType) {
        Call<MovieStore> callmoviedb;

        showMoviewGridView();

        ApiService apiService = RetroClient.getApiService();
        if (sortType == SortType.TOP_RATED) {
            callmoviedb = apiService.getTopRatedMovies(BuildConfig.ApiKey);

        } else {
            callmoviedb = apiService.getPopularMovies(BuildConfig.ApiKey);
        }

        mLoadingIndicator.setVisibility(View.VISIBLE);

        callmoviedb.enqueue(new Callback<MovieStore>() {
            @Override
            public void onResponse(Call<MovieStore> call, Response<MovieStore> response) {

                if (response.isSuccessful()) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);

                    if (response.body() != null) {
                        mMovieList = response.body().getResults();
                        popularMoviesAdapter = new PopularMoviesAdapter(mMovieList, MainActivity.this);

                        mPopMovieLayout.setAdapter(popularMoviesAdapter);
                    } else {
                        throw new HttpException(response);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieStore> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mPopMovieLayout.setVisibility(View.INVISIBLE);
                showErrorMessage();
            }
        });
    }


    private void showMoviewGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mPopMovieLayout.setVisibility(View.VISIBLE);
    }

    /**
     * This method will show an error message and hide the Popular Movies
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
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
