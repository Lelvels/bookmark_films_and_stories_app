package com.mrcong.bookmarkfilmsandcomicsapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.SingleMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.repositories.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    private MovieRepository movieRepository;

    public MovieListViewModel() {
        movieRepository = MovieRepository.getInstance();
    }
    public LiveData<List<SingleMovie>> getMovies(){
        return movieRepository.getMovieList();
    }
    public void getMoviesById(List<Integer> idList){
        movieRepository.getMovieListByIds(idList);
    }
}
