package com.mrcong.bookmarkfilmsandcomicsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.Movie;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.QueryMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.response.QueryMovieResponse;
import com.mrcong.bookmarkfilmsandcomicsapp.services.MovieApi;
import com.mrcong.bookmarkfilmsandcomicsapp.services.MovieApiClient;
import com.mrcong.bookmarkfilmsandcomicsapp.services.RetrofitService;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;
import com.mrcong.bookmarkfilmsandcomicsapp.viewmodels.MovieListViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //UI-components
    Button btn, btn2;
    //ViewModels
    private MovieListViewModel movieListViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
    }

    //Observing for any data changes
    private void ObserveAnyChange(){
        movieListViewModel.getMovies().observe(this, new Observer<List<QueryMovie>>() {
            @Override
            public void onChanged(List<QueryMovie> queryMovies) {
                //Observing for any data changes

            }
        });
    }
}