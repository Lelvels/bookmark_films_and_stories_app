package com.mrcong.bookmarkfilmsandcomicsapp.repositories;

import androidx.lifecycle.LiveData;

import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.SingleMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.services.MovieApiClient;

import java.util.List;

public class MovieRepository {
    private static MovieRepository instance;
    private MovieApiClient movieApiClient;
    private String previousSearchQuery;
    private int previousPageNumber;
    public static MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }
    private MovieRepository(){
        movieApiClient = MovieApiClient.getInstance();
    }
    //Called by View model
    public void searchMovieApi(String query, int pageNumber){
        previousSearchQuery = query;
        previousPageNumber = pageNumber;
        //Search for movies
        movieApiClient.searchMoviesApi(query, pageNumber);
    }
    public void searchNextPage(){
        searchMovieApi(previousSearchQuery, previousPageNumber +1);
    }
    public void requestSingleMovie(Integer movieId){
        movieApiClient.requestSingleMovieApi(movieId);
    }

    //Called by Live model
    public LiveData<List<MovieModel>> getSearchMovies(){
        return movieApiClient.getSearchMoviesLiveData();
    }
    public LiveData<SingleMovie> getSingleMovie(){
        return movieApiClient.getSingleMovieLiveData();
    }
}




