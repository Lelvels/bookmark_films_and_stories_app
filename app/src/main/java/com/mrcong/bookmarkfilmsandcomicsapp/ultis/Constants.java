package com.mrcong.bookmarkfilmsandcomicsapp.ultis;

public class Constants {
    public static class COMMON{
        public static final String TAG = "FileBookMarkApp";
        public static final String FAV_MOVIE_KEY = "favouriteMovies";
        public static final String USER_KEY = "Users";
    }

    public static class ARGS{
        public static final String PREVIOUS_ACTIVITY_BUNDLE_KEY = "previousActivity";
        public static final String POPULARS_FRAGMENT = "popular";
        public static final String SEARCH_FRAGMENT = "search";
        public static final String FAV_FRAGMENT = "fav";
    }

    public static class CREDENTIAL{
        public static final String API_KEY = "0fa5805b0e9842d3696f8aa336885bbb";
    }

    public static class BASE_URL{
        public static final String BASE_URL = "https://api.themoviedb.org";
        public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    }

    public static class HTTP_STATUS{
        public static final Integer OK = 200;
        public static final Integer NOT_FOUND = 404;
    }
}
