package com.udacity.android.popularmovies.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.udacity.android.popularmovies.R;
import com.udacity.android.popularmovies.model.Result;

import java.util.List;

import static com.google.android.youtube.player.YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION;

public class TrailerGalleryAdapter extends RecyclerView.Adapter<TrailerGalleryAdapter.TrailerViewHolder> {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private List<Result> mTrailerList;
    private Context context;


    public TrailerGalleryAdapter(Context context, List<Result> mTrailerList) {
        this.context = context;
        this.mTrailerList = mTrailerList;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_movie_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrailerViewHolder(view, YouTubePlayerFragment.newInstance());
    }


    @Override
    public void onViewAttachedToWindow(@NonNull TrailerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ((Activity) context).getFragmentManager().beginTransaction()
                .replace(holder.trailerView.getId(), holder.fragment).commitNow();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull TrailerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }

    @Override
    public void onViewRecycled(@NonNull TrailerViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder trailerViewHolder, int position) {
        final Result movieTrailer = mTrailerList.get(position);

        trailerViewHolder.setIsRecyclable(false);
        final YouTubePlayerFragment youtubeFragment = trailerViewHolder.fragment;
        //TODO : Save this key securely

        try {
            youtubeFragment.initialize("AIzaSyDfGzKHA-q4bldzopjnBdV7g2EG1lQIH5Q",
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            final YouTubePlayer player, boolean wasRestored) {

                            if (null == player) return;
                            if (!wasRestored) {

                                if (context.getClass().equals(MovieDetailsActivity.class)) {
                                    ((MovieDetailsActivity) context).youTubePlayer = player;
                                }
                                player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                                player.setFullscreenControlFlags(FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                                player.cueVideo(movieTrailer.getKey());

                                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                                    @Override
                                    public void onLoading() {

                                    }

                                    @Override
                                    public void onLoaded(String s) {

                                    }

                                    @Override
                                    public void onAdStarted() {

                                    }

                                    @Override
                                    public void onVideoStarted() {
                                        player.setFullscreen(true);
                                        player.play();
                                    }

                                    @Override
                                    public void onVideoEnded() {
                                        player.cueVideo(movieTrailer.getKey());
                                    }

                                    @Override
                                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                                    }
                                });

                                player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                                    @Override
                                    public void onFullscreen(boolean isFullScreenEnabled) {
                                        ((MovieDetailsActivity) context).isYouTubePlayerFullScreen = isFullScreenEnabled;

                                    }
                                });

                            }

                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {
                            if (youTubeInitializationResult.isUserRecoverableError()) {
                                youTubeInitializationResult.getErrorDialog((Activity) context, RECOVERY_DIALOG_REQUEST).show();
                            } else {
                                String errorMessage = String.format(
                                        "There was an error initializing the player (%1$s)",
                                        youTubeInitializationResult.toString());
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }


        trailerViewHolder.trailerView.bringToFront();
        trailerViewHolder.trailerView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        private CardView trailerView;
        private YouTubePlayerFragment fragment;

        public TrailerViewHolder(@NonNull View itemView, YouTubePlayerFragment fragment) {
            super(itemView);
            trailerView = itemView.findViewById(R.id.trailer_card);
            trailerView.setId(View.generateViewId());
            this.fragment = fragment;

        }
    }
}
