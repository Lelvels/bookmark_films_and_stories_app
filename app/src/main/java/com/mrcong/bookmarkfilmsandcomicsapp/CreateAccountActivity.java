package com.mrcong.bookmarkfilmsandcomicsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.AppUser;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.SingleMovie;
import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    private static final String TAG = Constants.COMMON.TAG;
    EditText emailTxt, passwordTxt, userNameTxt;
    Button createBtn, loginBtn;
    //Current movie
    SingleMovie singleMovie;
    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    //Firebase connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection(Constants.COMMON.USER_KEY);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        //Firebase Auth
        //Firebase Auth require google account to running
        firebaseAuth = FirebaseAuth.getInstance();

        //Widgets init
        passwordTxt = findViewById(R.id.caa_password);
        emailTxt = findViewById(R.id.caa_email);
        createBtn = findViewById(R.id.caa_signup_btn);
        userNameTxt = findViewById(R.id.caa_user_name);
        loginBtn = findViewById(R.id.caa_login_btn);
        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        //Authentication
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    //Already login
                } else {
                    //Create account
                }
            }
        };
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(emailTxt.getText().toString())
                && !TextUtils.isEmpty(passwordTxt.getText().toString())
                && !TextUtils.isEmpty(userNameTxt.getText().toString())){
                    String email = emailTxt.getText().toString().trim();
                    String password = passwordTxt.getText().toString().trim();
                    String userName = userNameTxt.getText().toString().trim();
                    createNewUser(email, password, userName);
                } else {
                    Toast.makeText(CreateAccountActivity.this,
                            "Email or password is empty!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void createNewUser(String email, String password, String userName){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Taking user to the next activity
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            final String currentUserId = currentUser.getUid();

                            //Create user map so we can create a user in the User Collection in Firestore
                            Map<String, String> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);
                            userObj.put("userName", userName);
                            //Adding users to FireStore
                            collectionReference.add(userObj).addOnSuccessListener(documentReference -> {
                                documentReference.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(Objects.requireNonNull(task.getResult()).exists()){
                                                    String name = task.getResult().getString("userName");
                                                    //If the user registered successfully
                                                    Intent intent = new Intent(CreateAccountActivity.this, MovieAppActivity.class);
                                                    AppUser.getInstance().setUserId(currentUserId);
                                                    AppUser.getInstance().setUserName(name);
                                                    startActivity(intent);
                                                } else {
                                                    Log.e(TAG,"Cannot register user to Firebase because the user is Null!");
                                                    Toast.makeText(CreateAccountActivity.this, "Cannot register, please try again later!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                                                Toast.makeText(CreateAccountActivity.this, "Cannot register because " + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            });
                        }
                    }
                });
    }
}