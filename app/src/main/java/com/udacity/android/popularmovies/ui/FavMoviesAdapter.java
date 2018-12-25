package com.udacity.android.popularmovies.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.udacity.android.popularmovies.R;
import com.udacity.android.popularmovies.model.MovieSnapshot;

import java.util.List;

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavMoviesAdapterViewHolder> {
    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private final FavMoviesAdapterOnClickHandler moviesAdapterOnClickHandler;
    private List<MovieSnapshot> mMovieStoreList;


    public FavMoviesAdapter(List<MovieSnapshot> mMovieStoreList, FavMoviesAdapterOnClickHandler onClickHandler) {
        this.mMovieStoreList = mMovieStoreList;
        moviesAdapterOnClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public FavMoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new FavMoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavMoviesAdapter.FavMoviesAdapterViewHolder popularMoviesAdapterViewHolder, int position) {
        MovieSnapshot movieSnapshot = mMovieStoreList.get(position);

        String moviePosterUrl = BASE_IMAGE_URL + movieSnapshot.getPosterPath();
        Context context = popularMoviesAdapterViewHolder.mPosterImageView.getContext();
        Picasso.with(context)
                .load(moviePosterUrl)
                .placeholder(R.drawable.popular_4k)
                .error(R.drawable.popular_4k)
                .into(popularMoviesAdapterViewHolder.mPosterImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieStoreList) return 0;
        return mMovieStoreList.size();
    }

    public void setMoviesData(List<MovieSnapshot> movieRecords) {
        mMovieStoreList = movieRecords;
        notifyDataSetChanged();
    }

    public interface FavMoviesAdapterOnClickHandler {
        void onClick(MovieSnapshot movieSnapshot);
    }

    public class FavMoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPosterImageView;

        public FavMoviesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_movie_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieSnapshot movieDetail = mMovieStoreList.get(adapterPosition);
            moviesAdapterOnClickHandler.onClick(movieDetail);
        }
    }
}
