package com.udacity.android.popularmovies.mediaplayer;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.android.popularmovies.R;


public class MediaPlayerImpl implements MediaPlayer {
    private ExoPlayer player;
    private Context context;


    private void initializePlayer() {

        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(context),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
        }
    }

    @Override
    public void play(String url) {
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(context, userAgent))
                .setExtractorsFactory(new DefaultExtractorsFactory())
                .createMediaSource(Uri.parse(url));
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    @Override
    public ExoPlayer getMediaPlayer(Context context) {
        this.context = context;
        initializePlayer();
        return player;
    }

    @Override
    public void releasePlayer() {
        player.stop();
        player.release();
    }
}
