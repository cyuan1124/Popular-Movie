package com.chefmic.movie.app;

/**
 * Created by chenyuan on 6/7/16.
 */
public class Constant {

    public static final String MD_PAGE = "page";
    public static final String MD_RESULTS = "results";
    public static final String MD_TOTAL_RESULTS = "total_results";
    public static final String MD_TOTAL_PAGES = "total_pages";
    public static final String MD_POSTER_PATH = "poster_path";
    public static final String MD_ADULT = "adult";
    public static final String MD_OVERVIEW = "overview";
    public static final String MD_RELEASE_DATE = "release_date";
    public static final String MD_GENRE_IDS = "genre_ids";
    public static final String MD_ID = "id";
    public static final String MD_ORIGINAL_TITLE = "original_title";
    public static final String MD_ORIGINAL_LANGUAGE = "original_language";
    public static final String MD_TITLE = "title";
    public static final String MD_BACKDROP_PATH = "backdrop_path";
    public static final String MD_POPULARITY = "popularity";
    public static final String MD_VOTE_COUNT = "vote_count";
    public static final String MD_VIDEO = "video";
    public static final String MD_VOTE_AVERAGE = "vote_average";

    public static final String IMG_BASE_URI = "http://image.tmdb.org/t/p/w185";
    public static final String BACKDROP_IMG_BASE_URI = "http://image.tmdb.org/t/p/w500";

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
