package com.mrcong.bookmarkfilmsandcomicsapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mrcong.bookmarkfilmsandcomicsapp.R;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;

import java.util.List;

public class MovieRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = Constants.COMMON.TAG;
    private List<MovieModel> mMovies;
    private OnMovieListener onMovieListener;

    public MovieRecyclerView(OnMovieListener onMovieListener) {
        this.onMovieListener = onMovieListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item,
                parent, false);
        return new MovieViewHolder(view, onMovieListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder = (MovieViewHolder) holder;
        ((MovieViewHolder) holder).title.setText(mMovies.get(position).getTitle());
        ((MovieViewHolder) holder).releaseDate.setText(mMovies.get(position).getReleaseDate());
        ((MovieViewHolder) holder).duration.setText(mMovies.get(position).getOriginalLanguage());
        //Voting rate from 0 to 10
        ((MovieViewHolder) holder).ratingBar.setRating(mMovies.get(position).getVoteAverage()/2);
        //Image view with glide library
        String posterURL = Constants.BASE_URL.IMAGE_BASE_URL + mMovies.get(position).getPosterPath();
        Log.v(TAG, "Get: " + posterURL);
        Glide.with(holder.itemView.getContext())
                .load(posterURL)
                .into(((MovieViewHolder) holder).movieImage);
    }

    public void setmMovies(List<MovieModel> mMovies) {
        this.mMovies = mMovies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mMovies != null){
            return mMovies.size();
        }
        return 0;
    }

    //Getting the id of the movie clicked
    public MovieModel getSelectedMovie(int position){
        if(mMovies != null){
            if(mMovies.size() > 0){
                return mMovies.get(position);
            }
        }
        return null;
    }
}
