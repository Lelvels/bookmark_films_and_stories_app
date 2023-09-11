package com.mrcong.bookmarkfilmsandcomicsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.AppUser;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = Constants.COMMON.TAG;
    //Widgets
    AutoCompleteTextView emailTxt;
    EditText passwordTxt;
    Button loginBtn, createAccountBtn;
    //Firebase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentFireBaseUser;

    //Firebase connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection(Constants.COMMON.USER_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailTxt = findViewById(R.id.la_email);
        passwordTxt = findViewById(R.id.la_password);
        loginBtn = findViewById(R.id.la_login_btn);
        createAccountBtn = findViewById(R.id.la_sign_up_btn);

        //Firebase init
        firebaseAuth = FirebaseAuth.getInstance();

        createAccountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> {
            loginWithEmailAndPassword(
                    emailTxt.getText().toString().trim(),
                    passwordTxt.getText().toString().trim()
            );
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentFireBaseUser = firebaseAuth.getCurrentUser();
        if(currentFireBaseUser!=null){
            final String currentUserId = currentFireBaseUser.getUid();
            collectionReference.whereEqualTo("userId", currentUserId)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null){
                                Log.e(TAG, error.toString());
                            }
                            assert value != null;
                            if(!value.isEmpty()){
                                //Getting all QueryDocSnapShots
                                for(QueryDocumentSnapshot snapshot : value){
                                    Log.v(TAG, "User found with uid: " + currentUserId);
                                    AppUser appUser = AppUser.getInstance();
                                    appUser.setUserName(snapshot.getString("userName"));
                                    appUser.setUserId(snapshot.getString("userId"));
                                    //After successfully login
                                    Intent intent = new Intent(LoginActivity.this, MovieAppActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
        } else {
            Log.v(TAG, "No user login yet!");
        }
    }

    private void loginWithEmailAndPassword(String email, String password) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
           firebaseAuth.signInWithEmailAndPassword(email, password)
                   .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           currentFireBaseUser = firebaseAuth.getCurrentUser();
                           assert currentFireBaseUser != null;
                           final String currentUserId = currentFireBaseUser.getUid();
                           collectionReference.whereEqualTo("userId", currentUserId)
                                   .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                       @Override
                                       public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                           if(error != null){
                                               Log.e(TAG, error.toString());
                                           }
                                           assert value != null;
                                           if(!value.isEmpty()){
                                               //Getting all QueryDocSnapShots
                                               for(QueryDocumentSnapshot snapshot : value){
                                                   AppUser appUser = AppUser.getInstance();
                                                   appUser.setUserName(snapshot.getString("userName"));
                                                   appUser.setUserId(snapshot.getString("userId"));
                                                   //After successfully login
                                                   Intent intent = new Intent(LoginActivity.this, MovieAppActivity.class);
                                                   startActivity(intent);
                                               }
                                           }
                                       }
                                   });
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Log.e(TAG, e.getMessage());
                           Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
        } else {
            Toast.makeText(LoginActivity.this, "Email or password is empty!", Toast.LENGTH_SHORT).show();
        }
    }
}