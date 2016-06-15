package com.chefmic.movie.app;

/**
 * Created by chenyuan on 6/7/16.
 */
public class Constant {

    public static final String FORECAST_BASE_URL =  "http://api.themoviedb.org";
    public static final String IMG_BASE_URI = "http://image.tmdb.org/t/p/w185";
    public static final String BACKDROP_IMG_BASE_URI = "http://image.tmdb.org/t/p/w500";

    //http://img.youtube.com/vi/7d_jQycdQGo/0.jpg
    public static final String YOUTUBE_THUMBNAIL_URI = "http://img.youtube.com/vi/%s/0.jpg";

    public enum Extras {
        Movie, MovieResult
    }

    public enum Intent {
        Settings(0);

        public final int code;

        Intent(int code) {
            this.code = code;
        }
    }
}
