package com.chefmic.movie.app;

import android.app.Application;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chenyuan on 6/12/16.
 */
public class MovieApplication extends Application {

    public static Retrofit retrofit;

    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.FORECAST_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();
    }

}
