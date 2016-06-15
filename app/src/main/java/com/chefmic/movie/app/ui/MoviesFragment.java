package com.chefmic.movie.app.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.chefmic.movie.app.Constant;
import com.chefmic.movie.app.MovieApplication;
import com.chefmic.movie.app.R;
import com.chefmic.movie.app.Utils;
import com.chefmic.movie.app.data.MovieContract;
import com.chefmic.movie.app.endpoint.FetchMovieService;
import com.chefmic.movie.app.parcelable.Movie;
import com.chefmic.movie.app.parcelable.MovieResult;
import com.chefmic.movie.app.ui.widget.MovieAdapter;
import com.chefmic.movie.app.ui.widget.MovieCursorAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chenyuan on 6/7/16.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface Callback {

        void onMovieSelected(Movie movie);

    }

    private static final int INIT_FAV_LIST_LOADER_ID = 0;

    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_POSTER_PATH = 2;
    public static final int COL_BACKDROP_URL = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_TITLE = 6;
    public static final int COL_VOTE_COUNT = 7;
    public static final int COL_VOTE_AVERAGE = 8;

    private ArrayAdapter<Movie> adapter;
    private MovieCursorAdapter cursorAdapter;
    private MovieResult movieResult;
    private String currentOrder;

    @BindView(R.id.movies_grid_view)
    GridView moviesGrid;
    @BindView(R.id.empty_view)
    View emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new MovieAdapter(getActivity());
        cursorAdapter = new MovieCursorAdapter(getActivity(), null, 0);
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, view);
        moviesGrid.setEmptyView(emptyView);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = null;
                if (moviesGrid.getAdapter() == adapter) {
                    movie = adapter.getItem(position);
                } else {
                    movie = cursorAdapter.getItem(position);
                }
                if (movie == null) {
                    return;
                }

                if (((MainActivity) getActivity()).isTwoPane()) {
                    ((MainActivity) getActivity()).onMovieSelected(movie);
                    return;
                }

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieResult != null) {
            outState.putParcelable(Constant.Extras.MovieResult.name(), movieResult);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!Utils.displayFavourite(getActivity())) {
            return;
        }
        cursorAdapter.swapCursor(data);
        ((MainActivity) getActivity()).onMovieSelected(cursorAdapter.getItem(0));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void loaded(MovieResult movieResult) {
        this.movieResult = movieResult;
        adapter.clear();
        if (movieResult != null && movieResult.getResults() != null && !movieResult.getResults().isEmpty()) {
            adapter.addAll(movieResult.getResults());
            ((MainActivity) getActivity()).onMovieSelected(adapter.getItem(0));
        } else {
            ((MainActivity) getActivity()).onMovieSelected(null);
        }
    }

    public void loadMovies() {
        String sortOrder = Utils.getSortOrder(getActivity());
        if (sortOrder.equals(currentOrder)) {
            return;
        }
        currentOrder = sortOrder;
        if (sortOrder.equals(getString(R.string.pref_sort_order_favourite))) {
            moviesGrid.setAdapter(cursorAdapter);
            getLoaderManager().initLoader(INIT_FAV_LIST_LOADER_ID, null, this);
            return;
        }
        moviesGrid.setAdapter(adapter);
        FetchMovieService fetchMovieService = MovieApplication.retrofit.create(FetchMovieService.class);
        Observable<MovieResult> call = fetchMovieService.getMovieResult(sortOrder);
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

    public void refreshFavList() {
        getLoaderManager().restartLoader(INIT_FAV_LIST_LOADER_ID, null, this);
    }
}
