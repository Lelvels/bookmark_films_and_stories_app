package com.mrcong.bookmarkfilmsandcomicsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mrcong.bookmarkfilmsandcomicsapp.fragments.FavouritesFragment;
import com.mrcong.bookmarkfilmsandcomicsapp.fragments.PopularFragment;
import com.mrcong.bookmarkfilmsandcomicsapp.fragments.SearchFragment;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.AppUser;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;

import java.util.Objects;

public class MovieAppActivity extends AppCompatActivity {
    private static final String TAG = Constants.COMMON.TAG;
    //widget
    private BottomNavigationView bottomNavigationView;
    private PopularFragment popularFragment;
    private SearchFragment searchFragment;
    private FavouritesFragment favouritesFragment;
    /*
    * Firebase connection
    * */
    //Firebase Auth
    private String currentUserId;
    private String currentUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currenFirebaseUser;
    //Firestore
    private FirebaseFirestore db;
    private CollectionReference movieCollectionRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_app);
        //Firestore init
        db = FirebaseFirestore.getInstance();
        movieCollectionRef = db.collection(Constants.COMMON.FAV_MOVIE_KEY);
        //Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        currenFirebaseUser = firebaseAuth.getCurrentUser();
        if(currenFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(AppUser.getInstance() != null){
            currentUserId = AppUser.getInstance().getUserId();
            currentUserName = AppUser.getInstance().getUserName();
            Log.v(TAG, "Current user credential: " + currentUserName);
        }

        //Init widgets
        bottomNavigationView = findViewById(R.id.maa_bottom_navigation);
        popularFragment = new PopularFragment();
        searchFragment = new SearchFragment();
        favouritesFragment = new FavouritesFragment();

        //Toolbar setup
        Toolbar toolbar = findViewById(R.id.maa_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.showOverflowMenu();
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        //Setup search view
        setupSearchView();

        //Setup navigation drawer
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_populars:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.maa_fl_fragment, popularFragment)
                                .commit();
                        return true;
                    case R.id.item_search:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.maa_fl_fragment, searchFragment)
                                .commit();
                        return true;
                    case R.id.item_favourite:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.maa_fl_fragment, favouritesFragment)
                                .commit();
                        return true;
                }
                return false;
            }
        });

        //Default fragment on launch
        Intent intent = getIntent();
        if(intent.hasExtra(Constants.ARGS.PREVIOUS_ACTIVITY_BUNDLE_KEY)){
            String previousFrag = intent.getStringExtra(Constants.ARGS.PREVIOUS_ACTIVITY_BUNDLE_KEY);
            if(previousFrag != null && !previousFrag.isEmpty()){
                if(previousFrag.equals(Constants.ARGS.FAV_FRAGMENT)){
                    bottomNavigationView.setSelectedItemId(R.id.item_favourite);
                } else if (previousFrag.equals(Constants.ARGS.POPULARS_FRAGMENT)) {
                    bottomNavigationView.setSelectedItemId(R.id.item_populars);
                } else if (previousFrag.equals(Constants.ARGS.SEARCH_FRAGMENT)) {
                    bottomNavigationView.setSelectedItemId(R.id.item_search);
                } else {
                    bottomNavigationView.setSelectedItemId(R.id.item_populars);
                }
            } else {
                bottomNavigationView.setSelectedItemId(R.id.item_populars);
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.item_populars);
        }
    }

    private void setupSearchView() {
        final SearchView searchView = findViewById(R.id.maa_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bottomNavigationView.setSelectedItemId(R.id.item_search);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    bottomNavigationView.setSelectedItemId(R.id.item_search);
                    searchFragment = SearchFragment.newInstance(newText);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.maa_fl_fragment, searchFragment)
                            .commit();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.tm_user){
            Toast.makeText(this, "Hello: " + currentUserName, Toast.LENGTH_SHORT).show();
        } else if(item.getItemId() == R.id.tm_sign_out){
            if(currenFirebaseUser != null && firebaseAuth != null){
                Log.v(TAG, "User: " + currentUserName + " logout!");
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currenFirebaseUser = firebaseAuth.getCurrentUser();
        if(currenFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}