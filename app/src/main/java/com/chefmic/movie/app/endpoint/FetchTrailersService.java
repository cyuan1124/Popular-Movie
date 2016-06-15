package com.chefmic.movie.app.endpoint;

import com.chefmic.movie.app.BuildConfig;
import com.chefmic.movie.app.parcelable.Trailers;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by chenyuan on 6/12/16.
 */
public interface FetchTrailersService {

    @GET("3/movie/{id}/videos?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
    Observable<Trailers> getTrailerResult(@Path("id") long movieId);

}
