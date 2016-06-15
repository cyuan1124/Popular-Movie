package com.chefmic.movie.app.endpoint;

import com.chefmic.movie.app.BuildConfig;
import com.chefmic.movie.app.parcelable.MovieResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by chenyuan on 6/7/16.
 */

public interface FetchMovieService {

    @GET("3/movie/{sort}?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
    Observable<MovieResult> getMovieResult(@Path("sort") String sort);
}