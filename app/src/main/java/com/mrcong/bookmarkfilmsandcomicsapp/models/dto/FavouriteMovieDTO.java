package com.mrcong.bookmarkfilmsandcomicsapp.models.dto;

import java.sql.Timestamp;
import java.time.Instant;

public class FavouriteMovieDTO {
    private String userId;
    private String userName;
    private Integer movieId;
    private String movieTitle;
    private Timestamp timestamp;

    public FavouriteMovieDTO() {
    }

    public FavouriteMovieDTO(String userId, String userName, Integer movieId, String movieTitle, Timestamp timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
