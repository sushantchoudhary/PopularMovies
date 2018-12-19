package com.udacity.android.popularmovies.mediaplayer;

import android.content.Context;
import com.google.android.exoplayer2.ExoPlayer;

public interface MediaPlayer {
     void play(String url) ;

     ExoPlayer getMediaPlayer(Context context);

     void releasePlayer();
}
