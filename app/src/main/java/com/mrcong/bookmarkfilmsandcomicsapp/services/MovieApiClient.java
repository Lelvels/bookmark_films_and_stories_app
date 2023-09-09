package com.mrcong.bookmarkfilmsandcomicsapp.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mrcong.bookmarkfilmsandcomicsapp.AppExecutors;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.SingleMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.response.MovieSearchResponse;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieApiClient {
    // LiveData
    private MutableLiveData<List<MovieModel>> searchMoviesLiveData;
    private MutableLiveData<SingleMovie> singleMovieLiveData;

    //Instance
    private static MovieApiClient instance;
    private static final String TAG = Constants.COMMON.TAG;

    // making Global RUNNABLE
    private RetrieveSearchMoviesRunnable retrieveSearchMoviesRunnable;
    private RetrieveSingleMovieRunnale retrieveSingleMovieRunnale;

    public static MovieApiClient getInstance(){
        if (instance == null){
            instance = new MovieApiClient();
        }
        return  instance;
    }

    private MovieApiClient(){
        searchMoviesLiveData = new MutableLiveData<>();
        singleMovieLiveData = new MutableLiveData<>();
    }

    //Getter for the live data, called by repository
    public LiveData<List<MovieModel>> getSearchMoviesLiveData(){
        return searchMoviesLiveData;
    }
    public LiveData<SingleMovie> getSingleMovieLiveData(){
        return singleMovieLiveData;
    }

    //These method that we are going to call through the classes
    public void searchMoviesApi(String query, int pageNumber) {
        if (retrieveSearchMoviesRunnable != null){
            retrieveSearchMoviesRunnable = null;
        }
        retrieveSearchMoviesRunnable = new RetrieveSearchMoviesRunnable(query, pageNumber);
        final Future myHandler = AppExecutors.getInstance().getmNetworkIO().submit(retrieveSearchMoviesRunnable);
        AppExecutors.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Cancelling the retrofit call
                myHandler.cancel(true);
            }
        }, 5000, TimeUnit.MILLISECONDS);
    }

    public void requestSingleMovieApi(Integer movieId){
        if(retrieveSingleMovieRunnale != null){
            retrieveSingleMovieRunnale = null;
        }
        retrieveSingleMovieRunnale = new RetrieveSingleMovieRunnale(movieId);
        final Future myHandler = AppExecutors.getInstance().getmNetworkIO().submit(retrieveSingleMovieRunnale);
        AppExecutors.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Cancelling the retrofit call
                myHandler.cancel(true);
            }
        }, 5000, TimeUnit.MILLISECONDS);
    }

    //Retrieving data from RestAPI by runnable class
    //Search movies using runnable class
    private class RetrieveSearchMoviesRunnable implements Runnable{
        private String query;
        private int pageNumber;
        boolean cancelRequest;
        private int maximumPageNumber;
        public RetrieveSearchMoviesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }
        @Override
        public void run() {
            if(cancelRequest){
                return;
            }
            RetrofitService.getMovieApi().searchMovie(Constants.CREDENTIAL.API_KEY, query, pageNumber).enqueue(new Callback<MovieSearchResponse>() {
                @Override
                public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                    List<MovieModel> movieModelList = response.body().getMovies();
                    movieModelList = movieModelList.stream()
                            .filter(movieModel -> movieModel.getPosterPath() != null)
                            .sorted(Comparator.comparing(MovieModel::getReleaseDate).reversed())
                            .collect(Collectors.toList());
                    maximumPageNumber = response.body().getTotalPage();
                    if(pageNumber <= maximumPageNumber){
                        if(pageNumber == 1){
                            searchMoviesLiveData.postValue(movieModelList);
                        } else {
                            List<MovieModel> currentMovies = searchMoviesLiveData.getValue();
                            currentMovies.addAll(movieModelList);
                            searchMoviesLiveData.postValue(currentMovies);
                        }
                    }
                }
                @Override
                public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                    Log.v(TAG, "Error: " + t.getMessage());
                    searchMoviesLiveData.postValue(null);
                }
            });
        }
    }

    //Get single movie using runnable class
    private class RetrieveSingleMovieRunnale implements Runnable{
        private Integer movieId;
        private boolean cancelRequest;
        public RetrieveSingleMovieRunnale(Integer movieId) {
            this.movieId = movieId;
        }
        private Call<SingleMovie> requestSingleMovies(){
            return RetrofitService.getMovieApi().findById(movieId, Constants.CREDENTIAL.API_KEY);
        }
        @Override
        public void run() {
            if(cancelRequest){
                return;
            }
            RetrofitService.getMovieApi().findById(movieId, Constants.CREDENTIAL.API_KEY).enqueue(new Callback<SingleMovie>() {
                @Override
                public void onResponse(Call<SingleMovie> call, Response<SingleMovie> response) {
                    SingleMovie singleMovie = (SingleMovie) response.body();
                    Log.v(TAG, "Request for single movie: " + singleMovie.toString());
                    singleMovieLiveData.postValue(singleMovie);
                }
                @Override
                public void onFailure(Call<SingleMovie> call, Throwable t) {
                    Log.v(TAG, "Error: " + t.getMessage());
                    searchMoviesLiveData.postValue(null);
                }
            });
        }
    }
}
