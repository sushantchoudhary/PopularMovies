package com.udacity.android.popularmovies.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.udacity.android.popularmovies.R;
import com.udacity.android.popularmovies.database.AppDatabase;
import com.udacity.android.popularmovies.model.MovieRecord;
import com.udacity.android.popularmovies.model.MovieSnapshot;
import com.udacity.android.popularmovies.model.MovieStore;
import com.udacity.android.popularmovies.network.ApiService;
import com.udacity.android.popularmovies.network.RetroClient;
import com.udacity.android.popularmovies.viewmodel.MovieSnapshotViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavMoviesActivity extends AppCompatActivity implements FavMoviesAdapter.FavMoviesAdapterOnClickHandler {


    static final int PICK_DETAILS_REQUEST = 4321;
    private static final String TAG = FavMoviesActivity.class.getSimpleName();
    private static final Integer SPAN_COUNT = 2;
    private static AppDatabase mDB;
    private static Boolean IsRestarting;
    private RecyclerView mFavMovieLayout;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private FavMoviesAdapter favMoviesAdapter;
    private SortType preferredSort;
    private List<Long> allMovies = Collections.emptyList() ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFavMovieLayout = findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT,
                GridLayoutManager.VERTICAL, false);
        mFavMovieLayout.setLayoutManager(gridLayoutManager);

        mFavMovieLayout.setHasFixedSize(true);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mDB = AppDatabase.getsInstance(getApplicationContext());

        FavMoviesActivity.this.setTitle("My favorites");

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            setupFavViewModel();
        } else {
            loadAllMovies();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //FIXME
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


    @Override
    public void onClick(MovieSnapshot movieSnapshot) {
        fetchMovieRecord(movieSnapshot);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.popular_movie, menu);
        return true;
    }


    /***
     * This method is overridden by FavMoviesActivity class to populate sorting menu option
     * @param item Menu item displayed in app bar
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        favMoviesAdapter.setMoviesData(null);
        Integer id = item.getItemId();
        if (id == R.id.action_sortbypopularity) {
            preferredSort = SortType.POPULAR;
        } else {
            preferredSort = SortType.TOP_RATED;
        }
        if (item.getItemId() == R.id.action_sortbyfavorites) {
            IsRestarting = false;
            setupFavViewModel();
            return true;
        }
        launchMainActivity(preferredSort);
        return true;
    }


    private void launchMainActivity(SortType sortType) {
        Context context = this;
        Class destinationClass = MainActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, sortType);
        startActivity(intentToStartDetailActivity);
    }

    private void setupFavViewModel() {
        MovieSnapshotViewModel viewModel = ViewModelProviders.of(this).get(MovieSnapshotViewModel.class);
        viewModel.getMovies().observe(this, movieRecords -> {
            Log.d(TAG, "Updating movies from LiveData in ViewModel");
            favMoviesAdapter = new FavMoviesAdapter(movieRecords, FavMoviesActivity.this);
            mFavMovieLayout.setAdapter(favMoviesAdapter);
            //TODO : Fix this call {Skipping layout, no adapter found..}
//            favMoviesAdapter.setMoviesData(movieRecords);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        IsRestarting = true;
        setupFavViewModel();
        //FIXME Handle view sync after updating favorite status, App crashes when unfavorite is selected on FavActivity

    }

    private void showMoviewGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mFavMovieLayout.setVisibility(View.VISIBLE);
    }


    private void fetchMovieRecord(MovieSnapshot movieSnapshot) {
        new MovieSnapshotVoidMovieRecordAsyncTask().execute(movieSnapshot);
    }

    public Observable<MovieStore> getAllMovies() {
        ApiService apiService = RetroClient.getApiService();

        return apiService.getRxPopularMovies(BuildConfig.ApiKey)
                .flatMap(m -> apiService.getRxTopRatedMovies(BuildConfig.ApiKey));
    }

    @SuppressLint("CheckResult")
    public void loadAllMovies() {
        showMoviewGridView();
        mLoadingIndicator.setVisibility(View.VISIBLE);

        ApiService apiService = RetroClient.getApiService();

        Observable<MovieStore> calltopdb = apiService.getRxTopRatedMovies(BuildConfig.ApiKey);
        Observable<MovieStore> callpopmoviedb = apiService.getRxPopularMovies(BuildConfig.ApiKey);

        calltopdb.flatMap((Function<MovieStore, Observable<MovieStore>>) movieStore -> {
             mDB.movieDao().insertMovieList(movieStore.getResults());
            return callpopmoviedb;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieStore>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MovieStore movieStore) {

                        Log.d(TAG, "onNext");
                        allMovies = mDB.movieDao().insertMovieList(movieStore.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        mFavMovieLayout.setVisibility(View.INVISIBLE);
                        showErrorMessage();
                    }

                    @Override
                    public void onComplete() {
                        /**
                         * Setting up UI from view model
                         */
                        if(!allMovies.isEmpty()) {
                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                            setupFavViewModel();
                        }
                    }
                });
    }

    private class MovieSnapshotVoidMovieRecordAsyncTask extends AsyncTask<MovieSnapshot, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(MovieSnapshot... movieSnapshots) {
            LiveData<MovieRecord> movieRecordLiveData = mDB.movieDao().loadMovieById(movieSnapshots[0].getId());
            movieRecordLiveData.observe(FavMoviesActivity.this, movieRecord -> {
                runOnUiThread(() -> {
                    Context context = FavMoviesActivity.this;
                    Class destinationClass = MovieDetailsActivity.class;
                    Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                    intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movieRecord);
                    startActivity(intentToStartDetailActivity);
                });

            });
            //FIXME State need to be updated to handle storage better
            return null;
        }

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
                .setTitle(R.string.network_error)
                .setMessage(R.string.network_error_msg)
                .setNegativeButton(R.string.error_dismiss_button, (dialog, which) -> finish()).create().show();
    }

}
