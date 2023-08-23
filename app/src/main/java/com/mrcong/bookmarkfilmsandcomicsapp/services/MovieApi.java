package com.mrcong.bookmarkfilmsandcomicsapp.services;

import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.Movie;
import com.mrcong.bookmarkfilmsandcomicsapp.response.QueryMovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {
    //Search for movies
    @GET("/3/search/movie")
    Call<QueryMovieResponse> searchMovie(
            @Query("api_key") String key,
            @Query("query") String query,
            @Query("page") Integer page
    );

    //Find movie by Id
    @GET("3/movie/{movie_id}?")
    Call<Movie> findById(
            @Path("movie_id") Long movieId,
            @Query("api_key") String key
    );
}
