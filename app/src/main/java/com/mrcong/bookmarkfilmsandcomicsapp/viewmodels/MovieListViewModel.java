package com.mrcong.bookmarkfilmsandcomicsapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.QueryMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.repositories.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    private final MovieRepository movieRepository;

    public MovieListViewModel() {
        this.movieRepository = MovieRepository.getInstance();
    }
    public LiveData<List<QueryMovie>> getMovies(){
        return movieRepository.getQueryMovies();
    }
}
