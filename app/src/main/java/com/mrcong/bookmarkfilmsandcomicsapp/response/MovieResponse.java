package com.mrcong.bookmarkfilmsandcomicsapp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;

// This class is for single movie request
public class MovieResponse {
    // 1- Finding the Movie Object

    @SerializedName("results")
    @Expose
    private MovieModel movie;
    public MovieModel getMovie() {
        return movie;
    }
    @Override
    public String toString() {
        return "MovieResponse{" +
                "movie=" + movie +
                '}';
    }
}
