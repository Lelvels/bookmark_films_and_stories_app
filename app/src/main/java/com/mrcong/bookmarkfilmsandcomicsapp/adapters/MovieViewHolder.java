package com.mrcong.bookmarkfilmsandcomicsapp.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrcong.bookmarkfilmsandcomicsapp.R;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //Widgets
    TextView title, releaseDate, duration;
    ImageView movieImage;
    RatingBar ratingBar;
    //Click Listener
    OnMovieListener onMovieListener;

    public MovieViewHolder(@NonNull View itemView, OnMovieListener onMovieListener) {
        super(itemView);
        this.onMovieListener = onMovieListener;
        title = itemView.findViewById(R.id.mli_movie_title);
        releaseDate = itemView.findViewById(R.id.mli_movie_release_date);
        duration = itemView.findViewById(R.id.mli_movie_duration);
        movieImage = itemView.findViewById(R.id.mli_movie_img);
        ratingBar = itemView.findViewById(R.id.mli_rating_bar);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onMovieListener.onMovieClick(getAdapterPosition());
    }
}
