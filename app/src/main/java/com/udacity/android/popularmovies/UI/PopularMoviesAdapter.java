package com.udacity.android.popularmovies.UI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.udacity.android.popularmovies.Model.MovieRecord;
import com.udacity.android.popularmovies.R;

import java.util.List;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesAdapterViewHolder> {

    private List<MovieRecord> mMovieStoreList;


    private final PopularMoviesAdapterOnClickHandler moviesAdapterOnClickHandler;
    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";


    public interface PopularMoviesAdapterOnClickHandler {
        void onClick(Integer movieUrl);
    }

    public PopularMoviesAdapter(List<MovieRecord> mMovieStoreList, PopularMoviesAdapterOnClickHandler onClickHandler) {
        this.mMovieStoreList = mMovieStoreList;
        moviesAdapterOnClickHandler = onClickHandler;
    }


    public class PopularMoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPosterImageView;

        public PopularMoviesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_movie_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieRecord movieDetail = mMovieStoreList.get(adapterPosition);

            moviesAdapterOnClickHandler.onClick(movieDetail.getId());

        }
    }


    @NonNull
    @Override
    public PopularMoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new PopularMoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularMoviesAdapterViewHolder popularMoviesAdapterViewHolder, int position) {
        MovieRecord movieRecord = mMovieStoreList.get(position);

        String moviePosterUrl = BASE_IMAGE_URL + movieRecord.getPosterPath();
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


    public void setMoviesData(List<MovieRecord> movieRecords) {
        mMovieStoreList = movieRecords;
        notifyDataSetChanged();
    }
}
