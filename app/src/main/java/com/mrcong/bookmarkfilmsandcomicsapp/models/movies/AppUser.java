package com.mrcong.bookmarkfilmsandcomicsapp.models.movies;

import android.app.Application;

public class AppUser extends Application {
    private String userName;
    private String userId;
    private static AppUser instance;
    public static AppUser getInstance(){
        if(instance == null){
            instance = new AppUser();
        }
        return instance;
    }

    public AppUser(){
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
