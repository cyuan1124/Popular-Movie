package com.chefmic.movie.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.chefmic.movie.app.BuildConfig;
import com.chefmic.movie.app.Constant;
import com.chefmic.movie.app.MovieApplication;
import com.chefmic.movie.app.R;
import com.chefmic.movie.app.data.FetchMovieService;
import com.chefmic.movie.app.data.Movie;
import com.chefmic.movie.app.data.MovieResult;
import com.chefmic.movie.app.ui.widget.MovieAdapter;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chenyuan on 6/7/16.
 */
public class MoviesFragment extends Fragment {

    private ArrayAdapter<Movie> adapter;
    private MovieResult movieResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new MovieAdapter(getActivity());
        GridView view = (GridView) inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, view);
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Constant.Extras.Movie.name(), movie);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), view, getString(R.string.movie_poster_transition));
                    getActivity().startActivity(intent, options.toBundle());
                } else {
                    getActivity().startActivity(intent);
                }
            }
        });

        if (movieResult != null && movieResult.getResults() != null) {
            adapter.addAll(movieResult.getResults());
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.Extras.MovieResult.name())) {
            movieResult = savedInstanceState.getParcelable(Constant.Extras.MovieResult.name());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieResult != null) {
            outState.putParcelable(Constant.Extras.MovieResult.name(), movieResult);
        }
        super.onSaveInstanceState(outState);
    }

    public void loaded(MovieResult movieResult) {
        this.movieResult = movieResult;
        adapter.clear();
        if (movieResult != null && movieResult.getResults() != null) {
            adapter.addAll(movieResult.getResults());
        }
    }

    public void loadMovies(String sort) {
        FetchMovieService fetchMovieService = MovieApplication.retrofit.create(FetchMovieService.class);
        Observable<MovieResult> call = fetchMovieService.getMovieResult(sort, BuildConfig.THE_MOVIE_DB_API_KEY);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), R.string.error_loading_movies, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(MovieResult movieResult) {
                        loaded(movieResult);
                    }
                });
    }
}
