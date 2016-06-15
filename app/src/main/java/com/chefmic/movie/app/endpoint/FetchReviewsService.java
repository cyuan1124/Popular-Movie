package com.chefmic.movie.app.endpoint;

import com.chefmic.movie.app.BuildConfig;
import com.chefmic.movie.app.parcelable.Reviews;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by chenyuan on 6/12/16.
 */
public interface FetchReviewsService {

    @GET("3/movie/{id}/reviews?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
    Observable<Reviews> getReviewResult(@Path("id") long movieId);

}
