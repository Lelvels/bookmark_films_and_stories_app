package com.mrcong.bookmarkfilmsandcomicsapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrcong.bookmarkfilmsandcomicsapp.MovieDetailsActivity;
import com.mrcong.bookmarkfilmsandcomicsapp.R;
import com.mrcong.bookmarkfilmsandcomicsapp.adapters.MovieRecyclerViewAdapter;
import com.mrcong.bookmarkfilmsandcomicsapp.adapters.OnMovieListener;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;
import com.mrcong.bookmarkfilmsandcomicsapp.viewmodels.SearchMovieListViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements OnMovieListener {
    private static final String TAG = Constants.COMMON.TAG;
    private SearchMovieListViewModel searchMovieListViewModel;
    private static final String ARG_QUERY = "query";
    private String query;
    private RecyclerView recyclerView;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;

    public SearchFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramQuery query to search for movie.
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance(String paramQuery) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, paramQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(ARG_QUERY);
        }
        //Live model
        searchMovieListViewModel = new ViewModelProvider(this).get(SearchMovieListViewModel.class);
        observeAnyChange();
    }

    private void observeAnyChange() {
        searchMovieListViewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                Log.v(TAG, "Search movies adapter size: " + movieModels.size());
                movieRecyclerViewAdapter.setmMovies(movieModels);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.fs_recycler_view);
        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(this);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    searchMovieListViewModel.searchNextPage();
                }
            }
        });
        searchMovieListViewModel.searchMovieApi(query, 1);
        return view;
    }

    @Override
    public void onMovieClick(int positions) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("movieId",
                movieRecyclerViewAdapter.getSelectedMovie(positions).getId());
        startActivity(intent);
    }
}