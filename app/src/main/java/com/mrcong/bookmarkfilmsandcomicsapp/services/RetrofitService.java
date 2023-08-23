package com.mrcong.bookmarkfilmsandcomicsapp.services;

import com.mrcong.bookmarkfilmsandcomicsapp.ultis.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Constants.CREDENTIAL.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();
    private static MovieApi movieApi = retrofit.create(MovieApi.class);
    public static MovieApi getMovieApi() {
        return movieApi;
    }
}
