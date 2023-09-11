package com.mrcong.bookmarkfilmsandcomicsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mrcong.bookmarkfilmsandcomicsapp.models.dto.FavouriteMovieDTO;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.AppUser;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.Genre;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.SingleMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;
import com.mrcong.bookmarkfilmsandcomicsapp.viewmodels.SingleMovieViewModel;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class MovieDetailsActivity extends AppCompatActivity {
    //Widgets
    private static final String TAG = Constants.COMMON.TAG;
    private ImageView moviePoster;
    private TextView movieTitle, movieOverview, movieGenre;
    private RatingBar ratingBar;
    private SingleMovieViewModel singleMovieViewModel;
    private Integer movieId;
    private Button addFavBtn;

    //Current movie
    private SingleMovie currentMovie;
    private String fireStoreDocPath;

    //User id and user name
    private String currentUserId;
    private String currentUserName;
    //Firebase user
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currenFirebaseUser;
    //Firestore
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        //Widgets
        moviePoster = findViewById(R.id.amd_movie_poster);
        movieTitle = findViewById(R.id.amd_movie_title);
        movieOverview = findViewById(R.id.amd_movie_overview);
        ratingBar = findViewById(R.id.amd_rating_bar);
        movieGenre = findViewById(R.id.amd_movie_genre);
        addFavBtn = findViewById(R.id.amd_add_to_favourite);

        //Firebase
        db =  FirebaseFirestore.getInstance();
        collectionReference = db.collection(Constants.COMMON.FAV_MOVIE_KEY);
        firebaseAuth = FirebaseAuth.getInstance();
        if(AppUser.getInstance()!=null){
            currentUserId = AppUser.getInstance().getUserId();
            currentUserName = AppUser.getInstance().getUserName();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currenFirebaseUser = firebaseAuth.getCurrentUser();
                if(currenFirebaseUser != null){
                    Log.v(TAG, "User " + currenFirebaseUser.getUid() + " login!");
                } else {
                    Log.v(TAG, "User not login!");
                }
            }
        };

        //Getting movieId
        singleMovieViewModel = new ViewModelProvider(this).get(SingleMovieViewModel.class);
        getMovieId();
        singleMovieViewModel.requestSingleMovie(movieId);
        observeAnyChange();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currenFirebaseUser = firebaseAuth.getCurrentUser();
        if(currenFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
        }
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void observeAnyChange() {
        singleMovieViewModel.getSingleMovie().observe(this, new Observer<SingleMovie>() {
            @Override
            public void onChanged(SingleMovie singleMovie) {
                if(singleMovie != null){
                    currentMovie = singleMovie;
                    Log.v(Constants.COMMON.TAG, "Movie got: " + singleMovie.toString());
                    movieTitle.setText(singleMovie.getTitle());
                    movieOverview.setText(singleMovie.getOverview());
                    //Category String
                    String genreString = "Category: ";
                    List<Genre> genreList = singleMovie.getGenres();
                    for(Genre genre : genreList){
                        genreString = genreString + " " + genre.getName() + ",";
                    }
                    genreString = genreString.trim();
                    if(!genreString.isEmpty()) genreString = genreString.substring(0, genreString.length()-1);
                    movieGenre.setText(genreString);
                    //Movie poster
                    Glide.with(getApplicationContext())
                            .load(Constants.BASE_URL.IMAGE_BASE_URL + singleMovie.getPosterPath())
                            .into(moviePoster);
                    ratingBar.setRating(singleMovie.getVoteAverage()*1/2);

                    //Set first
                    addFavBtn.setText(getResources().getString(R.string.add_to_your_favourite_text));
                    addFavBtn.setOnClickListener(v -> {
                        saveFavMovie();
                    });

                    //Check if the single movie is favourite?
                    if(AppUser.getInstance() != null){
                        collectionReference.whereEqualTo("userId", AppUser.getInstance().getUserId())
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(error != null){
                                            Log.e(TAG, error.toString());
                                        }
                                        assert value != null;
                                        if(!value.isEmpty()){
                                            for(QueryDocumentSnapshot snapshot : value){
                                                Integer movieId = snapshot.getLong("movieId").intValue();
                                                if(movieId.equals(currentMovie.getId())){
                                                    addFavBtn.setText(getResources().getString(R.string.remove_to_your_favourite));
                                                    fireStoreDocPath = snapshot.getId();
                                                    addFavBtn.setOnClickListener(v -> {
                                                        removeFavMovie();
                                                    });
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void saveFavMovie() {
        FavouriteMovieDTO favouriteMovieDTO = new FavouriteMovieDTO();
        favouriteMovieDTO.setMovieTitle(currentMovie.getTitle());
        favouriteMovieDTO.setMovieId(currentMovie.getId());
        favouriteMovieDTO.setTimestamp(new Timestamp(System.currentTimeMillis()));
        favouriteMovieDTO.setUserName(currentUserName);
        favouriteMovieDTO.setUserId(currentUserId);
        //Add to firebase collection
        collectionReference.add(favouriteMovieDTO)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MovieDetailsActivity.this, "Movie added to your " +
                                "favourite list!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MovieDetailsActivity.this, "Failed to add movie " +
                                "favourite list!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                });
    }

    private void removeFavMovie() {
        collectionReference.document(fireStoreDocPath).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MovieDetailsActivity.this,
                                "Movie removed from your playlist successfully!",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess:" + " movie removed from user's playlist");

                        Intent intent = new Intent(MovieDetailsActivity.this,
                                MovieAppActivity.class);
                        intent.putExtra(Constants.ARGS.PREVIOUS_ACTIVITY_BUNDLE_KEY,
                                        Constants.ARGS.FAV_FRAGMENT);;
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MovieDetailsActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                });
    }

    private void getMovieId() {
        if(getIntent().hasExtra("movieId")){
            movieId = getIntent().getIntExtra("movieId", 0);
            Log.v(Constants.COMMON.TAG, "Get movie Id: " + movieId);
        } else {
            Log.e(TAG, "Error while taking movie Id");
        }
    }
}