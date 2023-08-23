package com.mrcong.bookmarkfilmsandcomicsapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mrcong.bookmarkfilmsandcomicsapp.models.movies.QueryMovie;

import java.util.List;

public class QueryMovieResponse {
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<QueryMovie> queryMovies;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<QueryMovie> getQueryMovies() {
        return queryMovies;
    }

    public void setQueryMovies(List<QueryMovie> queryMovies) {
        this.queryMovies = queryMovies;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public String toString() {
        return "QueryMovieResponse{" +
                "page=" + page +
                ", results=" + queryMovies +
                ", totalPages=" + totalPages +
                ", totalResults=" + totalResults +
                '}';
    }
}
