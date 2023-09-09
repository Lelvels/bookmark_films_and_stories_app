package com.mrcong.bookmarkfilmsandcomicsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.SingleMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;
import com.mrcong.bookmarkfilmsandcomicsapp.viewmodels.SingleMovieViewModel;

public class MovieDetailsActivity extends AppCompatActivity {
    //Widgets
    private static final String TAG = Constants.COMMON.TAG;
    private ImageView moviePoster;
    private TextView movieTitle, movieOverview;
    private RatingBar ratingBar;
    private SingleMovieViewModel singleMovieViewModel;
    private Integer movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        moviePoster = findViewById(R.id.amd_movie_poster);
        movieTitle = findViewById(R.id.amd_movie_title);
        movieOverview = findViewById(R.id.amd_movie_overview);
        ratingBar = findViewById(R.id.amd_rating_bar);

        //Getting movieId
        singleMovieViewModel = new ViewModelProvider(this).get(SingleMovieViewModel.class);
        getMovieId();
        singleMovieViewModel.requestSingleMovie(movieId);
        observeAnyChange();
    }

    private void observeAnyChange() {
        singleMovieViewModel.getSingleMovie().observe(this, new Observer<SingleMovie>() {
            @Override
            public void onChanged(SingleMovie singleMovie) {
                if(singleMovie != null){
                    Log.v(Constants.COMMON.TAG, "Movie got: " + singleMovie.toString());
                    movieTitle.setText(singleMovie.getTitle());
                    movieOverview.setText(singleMovie.getOverview());
                    Glide.with(getApplicationContext())
                            .load(Constants.BASE_URL.IMAGE_BASE_URL + singleMovie.getPosterPath())
                            .into(moviePoster);
                    ratingBar.setRating(singleMovie.getVoteAverage()*1/2);
                }
            }
        });
    }

    private void getMovieId() {
        if(getIntent().hasExtra("movieId")){
            movieId = getIntent().getIntExtra("movieId", 0);
            Log.v(Constants.COMMON.TAG, "Get movie Id: " + movieId);
        } else {
            Log.e(TAG, "Error while taking movie Id");
        }
    }
}