package com.mrcong.bookmarkfilmsandcomicsapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.MovieModel;

import java.util.List;

// This class is for getting multiple movies (Movies list) - popular movies
public class MovieSearchResponse {
    @SerializedName("total_results")
    @Expose()
    private int totalCount;

    @SerializedName("results")
    @Expose()
    private List<MovieModel> movies;

    @SerializedName("total_pages")
    @Expose()
    private int totalPage;

    public int getTotalCount(){
        return totalCount;
    }
    public List<MovieModel> getMovies(){
        return movies;
    }
    public int getTotalPage(){return totalPage;}

    @Override
    public String toString() {
        return "MovieSearchResponse{" +
                "totalCount=" + totalCount +
                ", movies=" + movies +
                ", totalPage=" + totalPage +
                '}';
    }
}
