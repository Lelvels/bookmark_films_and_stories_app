package com.mrcong.bookmarkfilmsandcomicsapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mrcong.bookmarkfilmsandcomicsapp.LoginActivity;
import com.mrcong.bookmarkfilmsandcomicsapp.MovieAppActivity;
import com.mrcong.bookmarkfilmsandcomicsapp.MovieDetailsActivity;
import com.mrcong.bookmarkfilmsandcomicsapp.R;
import com.mrcong.bookmarkfilmsandcomicsapp.adapters.FavouriteMovieAdapter;
import com.mrcong.bookmarkfilmsandcomicsapp.adapters.MovieRecyclerViewAdapter;
import com.mrcong.bookmarkfilmsandcomicsapp.adapters.OnMovieListener;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.AppUser;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.SingleMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;
import com.mrcong.bookmarkfilmsandcomicsapp.viewmodels.MovieListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment implements OnMovieListener {
    private static final String TAG = Constants.COMMON.TAG;
    //Live model
    private MovieListViewModel movieListViewModel;
    //Firestore
    private FirebaseFirestore db;
    private CollectionReference movieCollectionRef;
    //Recycle view
    private RecyclerView recyclerView;
    private FavouriteMovieAdapter favouriteMovieAdapter;

    public FavouritesFragment() {
    }

    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        observeAnyChange();
    }

    private void observeAnyChange() {
        movieListViewModel.getMovies().observe(this, new Observer<List<SingleMovie>>() {
            @Override
            public void onChanged(List<SingleMovie> singleMovies) {
                Log.v(TAG, "Favourite movies adapter size:" + singleMovies.size());
                favouriteMovieAdapter.setmMovies(singleMovies);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        recyclerView = view.findViewById(R.id.ff_recycle_view);
        favouriteMovieAdapter = new FavouriteMovieAdapter(this);
        recyclerView.setAdapter(favouriteMovieAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Init for firebase
        db = FirebaseFirestore.getInstance();
        movieCollectionRef = db.collection(Constants.COMMON.FAV_MOVIE_KEY);

        if(AppUser.getInstance() != null){
            movieCollectionRef.whereEqualTo("userId", AppUser.getInstance().getUserId())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null){
                                Log.e(TAG, error.toString());
                            }
                            assert value != null;
                            List<Integer> favouriteMovieIds = new ArrayList<>();
                            if(!value.isEmpty()){
                                //Getting all QueryDocSnapShots
                                for(QueryDocumentSnapshot snapshot : value){
                                    Log.v(TAG, "Favourite movie found: " + snapshot.getLong("movieId").intValue());
                                    favouriteMovieIds.add(snapshot.getLong("movieId").intValue());
                                }
                            }
                            Log.v(TAG, "Fragment movieIds: " + favouriteMovieIds.size());
                            movieListViewModel.getMoviesById(favouriteMovieIds);
                        }
                    });
        }
        return view;
    }


    @Override
    public void onMovieClick(int positions) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("movieId",
                favouriteMovieAdapter.getSelectedMovie(positions).getId());
        startActivity(intent);
    }
}