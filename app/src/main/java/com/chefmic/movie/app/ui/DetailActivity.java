package com.chefmic.movie.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.chefmic.movie.app.R;

/**
 * Created by chenyuan on 6/7/16.
 */
public class DetailActivity extends AppCompatActivity {
    private final String FRAGMENT_TAG = "detail.fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment(), FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            finish();
        } else {
            ((DetailFragment) fragment) .finish();
        }
    }
}
