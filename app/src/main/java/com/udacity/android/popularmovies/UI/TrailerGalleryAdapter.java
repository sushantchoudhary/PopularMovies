package com.udacity.android.popularmovies.UI;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.udacity.android.popularmovies.Model.MovieRecord;
import com.udacity.android.popularmovies.Model.Result;
import com.udacity.android.popularmovies.R;

import java.util.List;

public class TrailerGalleryAdapter extends RecyclerView.Adapter<TrailerGalleryAdapter.TrailerViewHolder> {

    private List<Result> mTrailerList;
    private Context context;

    public TrailerGalleryAdapter(Context context , List<Result> mTrailerList) {
        this.context = context;
        this.mTrailerList = mTrailerList;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_movie_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder trailerViewHolder, int position) {
        Result movieTrailer = mTrailerList.get(position);

        final YouTubePlayerFragment youtubeFragment;

        youtubeFragment = (YouTubePlayerFragment) ((Activity) context)
                .getFragmentManager().findFragmentById(R.id.video_fragment);


    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.video_parent_layout);
            }
    }
}
