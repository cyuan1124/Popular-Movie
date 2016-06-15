package com.chefmic.movie.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.chefmic.movie.app.Constant;
import com.chefmic.movie.app.R;
import com.chefmic.movie.app.Utils;
import com.chefmic.movie.app.parcelable.Movie;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {

    private static final String MOVIES_FRAGMENT_TAG = "movies.fragment";
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "detail.fragment";
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        twoPane = findViewById(R.id.movie_detail_fragment) != null;
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment(), MOVIES_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favourite) {
            loadMovies(getString(R.string.pref_sort_order_favourite));
            return true;
        } else if (id == R.id.action_most_popular) {
            loadMovies(getString(R.string.pref_sort_order_popular));
            return true;
        } else if (id == R.id.action_top_rated) {
            loadMovies(getString(R.string.pref_sort_order_rate));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(final Movie movie) {
        if (!twoPane) {
            return;
        }
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                if (movie == null) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG);
                    if (fragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                    return;
                }
                DetailFragment fragment = new DetailFragment();
                Bundle args = new Bundle();
                args.putParcelable(Constant.Extras.Movie.name(), movie);
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_fragment, fragment, MOVIE_DETAIL_FRAGMENT_TAG).commit();
            }
        });
    }

    public boolean isTwoPane() {
        return twoPane;
    }

    private void loadMovies(String sortOrder) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(getString(R.string.pref_sort_key), sortOrder);
        editor.apply();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOVIES_FRAGMENT_TAG);
        if (fragment != null) {
            ((MoviesFragment) fragment).loadMovies();
        }
    }

    public void reloadData() {
        if (twoPane && Utils.displayFavourite(this)) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOVIES_FRAGMENT_TAG);
            if (fragment != null) {
                ((MoviesFragment) fragment).refreshFavList();
            }
        }
    }
}
