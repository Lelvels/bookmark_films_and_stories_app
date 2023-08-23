package com.mrcong.bookmarkfilmsandcomicsapp.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mrcong.bookmarkfilmsandcomicsapp.AppExecutors;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.Movie;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.QueryMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.response.QueryMovieResponse;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {
    private static MovieApiClient instance;
    private final String TAG = "MovieApiClient";
    private MutableLiveData<List<QueryMovie>> queryMovies;

    //Making Global RUNNABLE
    private RetrieveMoviesRunnable retrieveMoviesRunnable;

    public static MovieApiClient getInstance() {
        if (instance == null){
            instance = new MovieApiClient();
        }
        return instance;
    }

    private MovieApiClient(){
        queryMovies = new MutableLiveData<>();
    }

    public LiveData<List<QueryMovie>> getQueryMovies(){
        return queryMovies;
    }

    public void searchMoviesApi(String query, int pageNumber){
        if (retrieveMoviesRunnable != null){
            retrieveMoviesRunnable = null;
        }
        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query, pageNumber);
        final Future myHandler = AppExecutors.getInstance().getmNetworkIO().submit(retrieveMoviesRunnable);
        //In 4s, if there isn't any request, no connections, low memory, cancel the handler.
        AppExecutors.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //Canceling the retrofit call
                myHandler.cancel(true);
            }
        }, 5000, TimeUnit.MICROSECONDS);
    }

    private class RetrieveMoviesRunnable implements Runnable{
        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveMoviesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getQueryMovies(query, pageNumber).execute();
                if(cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    List<QueryMovie> list = new ArrayList<>(((QueryMovieResponse)response.body())
                            .getQueryMovies());
                    if(pageNumber == 1){
                        //Sending data to live data
                        //Post value: used for background thread
                        //Set values: not for background thread
                        queryMovies.postValue(list);
                    } else {
                        List<QueryMovie> currentMovies = queryMovies.getValue();
                        currentMovies.addAll(list);
                        queryMovies.postValue(currentMovies);
                    }
                } else {
                    String error = response.errorBody().string();
                    Log.v(TAG, "Error: " + error);
                    queryMovies.postValue(null);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        private Call<QueryMovieResponse> getQueryMovies(String query, int pageNumber){
            return RetrofitService.getMovieApi().searchMovie(
                    Constants.CREDENTIAL.API_KEY,
                    query,
                    pageNumber
            );
        }
        private void doCancelRequest(){
            Log.v(TAG, "Canceling Search Request");
            cancelRequest = true;
        }
    }
}
