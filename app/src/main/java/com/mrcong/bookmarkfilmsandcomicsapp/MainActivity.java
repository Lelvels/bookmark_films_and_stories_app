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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mrcong.bookmarkfilmsandcomicsapp.adapters.MovieRecyclerViewAdapter;
import com.mrcong.bookmarkfilmsandcomicsapp.adapters.OnMovieListener;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;
import com.mrcong.bookmarkfilmsandcomicsapp.viewmodels.SearchMovieListViewModel;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMovieListener {
    private static final String TAG = Constants.COMMON.TAG;
    private SearchMovieListViewModel searchMovieListViewModel;
    private RecyclerView recyclerView;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;
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
        searchMovieListViewModel = new ViewModelProvider(this).get(SearchMovieListViewModel.class);
        observeAnyChange();
        configureRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.tm_user){
            Intent intent = new Intent(this, MovieAppActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView() {
        final SearchView searchView = findViewById(R.id.am_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMovieListViewModel.searchMovieApi(query, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchMovieListViewModel.searchMovieApi(newText, 1);
                return false;
            }
        });
    }

    //Observing for any data changes
    private void observeAnyChange(){
        searchMovieListViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                if (movieModels != null){
                    movieRecyclerViewAdapter.setmMovies(movieModels);
                }
            }
        });
    }

    private void configureRecyclerView(){
        //Live data cannot pass via the constructors
        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(this);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //RecyclerView Pagination
        //Loading next page of api response
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    searchMovieListViewModel.searchNextPage();
                }
            }
        });
    }

    @Override
    public void onMovieClick(int positions) {
        Toast.makeText(this, "Position: " + positions, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieId", movieRecyclerViewAdapter.getSelectedMovie(positions).getId());
        startActivity(intent);
    }
}