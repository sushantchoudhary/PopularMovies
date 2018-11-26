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
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.udacity.android.popularmovies.Model.MovieRecord;
import com.udacity.android.popularmovies.R;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView mMoviePosterIV;
    private TextView mReleaseYear;
    private TextView mRating;
    private TextView mPlotSynopsis;
    private MovieRecord movieRecord;
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
        mRating = findViewById(R.id.movie_rating);
        mPlotSynopsis = findViewById(R.id.plot_synopsis);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mMovieTitle = findViewById(R.id.movie_title);

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

        this.setTitle("Movie Detail");

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
                .into(mMoviePosterIV);

        mMovieTitle.setText(movieRecord.getOriginalTitle());
        mReleaseYear.setText(movieRecord.getReleaseDate().split("-")[0]);
        mRating.setText(Double.toString(movieRecord.getVoteAverage()) + getString(R.string.max_rating));
        mPlotSynopsis.setText(movieRecord.getOverview());
    }

    private void showMoviewDetailsView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailsLayout.setVisibility(View.VISIBLE);
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
