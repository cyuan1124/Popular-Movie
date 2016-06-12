package com.chefmic.movie.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.chefmic.movie.app.R;

public class MainActivity extends AppCompatActivity {

    private static final String MOVIES_FRAGMENT_TAG = "movies.fragment";
    private String currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment(), MOVIES_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMovies() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_order_default));
        if (!sortOrder.equals(currentOrder)) {
            currentOrder = sortOrder;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOVIES_FRAGMENT_TAG);
            if (fragment != null) {
                ((MoviesFragment) fragment).loadMovies(sortOrder);
            }
        }
    }
}
