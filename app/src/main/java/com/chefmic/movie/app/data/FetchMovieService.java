package com.chefmic.movie.app.data;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chenyuan on 6/7/16.
 */

public interface FetchMovieService {

    @GET("3/movie/{sort}")
    Observable<MovieResult> getMovieResult(@Path("sort") String sort,
                                           @Query("api_key") String apiKey);
}