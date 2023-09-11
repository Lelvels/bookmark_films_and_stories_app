package com.mrcong.bookmarkfilmsandcomicsapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;
import com.mrcong.bookmarkfilmsandcomicsapp.repositories.MovieRepository;

import java.util.List;

public class SearchMovieListViewModel extends ViewModel {
    private MovieRepository movieRepository;
    public SearchMovieListViewModel() {
        movieRepository = MovieRepository.getInstance();
    }
    //Function to get live data update, called by activity
    public LiveData<List<MovieModel>> getMovies(){
        return movieRepository.getSearchMovies();
    }
    //Calling method in view-model, called by activity
    public void searchMovieApi(String query, int pageNumber){
        movieRepository.searchMovieApi(query, pageNumber);
    }
    public void searchNextPage(){
        movieRepository.searchNextPage();
    }
}
