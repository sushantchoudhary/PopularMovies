package com.udacity.android.popularmovies.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.youtube.player.*;
import com.udacity.android.popularmovies.R;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);
    }
}
