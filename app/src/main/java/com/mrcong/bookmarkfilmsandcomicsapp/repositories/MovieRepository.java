package com.mrcong.bookmarkfilmsandcomicsapp.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.Movie;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.QueryMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.services.MovieApiClient;

import java.util.List;

public class MovieRepository {
    private static MovieRepository instance;
    private MutableLiveData<List<QueryMovie>> queryMovies;
    private MovieApiClient movieApiClient;
    public static MovieRepository getInstance(){
        if(instance == null){
            instance = new MovieRepository();
        }
        return instance;
    }
    private MovieRepository(){
        queryMovies = new MutableLiveData<>();
    }
    public LiveData<List<QueryMovie>> getQueryMovies(){
        return movieApiClient.getQueryMovies();
    }
}
