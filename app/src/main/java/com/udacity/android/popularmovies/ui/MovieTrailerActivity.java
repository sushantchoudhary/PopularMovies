package com.udacity.android.popularmovies.ui;

import android.os.Bundle;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.udacity.android.popularmovies.R;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);
    }
}
