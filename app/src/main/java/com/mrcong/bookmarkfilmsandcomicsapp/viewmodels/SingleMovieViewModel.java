package com.mrcong.bookmarkfilmsandcomicsapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.SingleMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.repositories.MovieRepository;

import java.util.List;

public class SingleMovieViewModel extends ViewModel {
    private MovieRepository movieRepository;
    public SingleMovieViewModel(){
        movieRepository = MovieRepository.getInstance();
    }
    public LiveData<SingleMovie> getSingleMovie(){
        return movieRepository.getSingleMovie();
    }
    public void requestSingleMovie(Integer movieId){
        movieRepository.requestSingleMovie(movieId);
    }
}
