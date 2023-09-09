package com.mrcong.bookmarkfilmsandcomicsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.mrcong.bookmarkfilmsandcomicsapp.adapters.MovieRecyclerView;
import com.mrcong.bookmarkfilmsandcomicsapp.adapters.OnMovieListener;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;
import com.mrcong.bookmarkfilmsandcomicsapp.viewmodels.MovieListViewModel;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMovieListener {
    private static final String TAG = Constants.COMMON.TAG;
    private MovieListViewModel movieListViewModel;
    private RecyclerView recyclerView;
    private MovieRecyclerView movieRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.am_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.showOverflowMenu();
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        //Search view setup
        setupSearchView();

        //Setup recycle view
        recyclerView = findViewById(R.id.am_recycler_view);
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        observeAnyChange();
        configureRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView() {
        final SearchView searchView = findViewById(R.id.am_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                movieListViewModel.searchMovieApi(query, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                movieListViewModel.searchMovieApi(newText, 1);
                return false;
            }
        });
    }

    //Observing for any data changes
    private void observeAnyChange(){
        movieListViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                if (movieModels != null){
                    for (MovieModel movieModel: movieModels){
                        movieRecyclerViewAdapter.setmMovies(movieModels);
                    }
                }
            }
        });
    }

    private void configureRecyclerView(){
        //Live data cannot pass via the constructors
        movieRecyclerViewAdapter = new MovieRecyclerView(this);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //RecyclerView Pagination
        //Loading next page of api response
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    movieListViewModel.searchNextPage();
                }
            }
        });
    }

    @Override
    public void onMovieClick(int positions) {
        //Toast.makeText(this, "Position: " + positions, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieId", movieRecyclerViewAdapter.getSelectedMovie(positions).getId());
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {

    }
}