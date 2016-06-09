package com.chefmic.movie.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chefmic.movie.app.R;

/**
 * Created by chenyuan on 6/8/16.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }

}
