package com.chefmic.movie.app.ui.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.chefmic.movie.app.Constant;
import com.chefmic.movie.app.R;
import com.chefmic.movie.app.data.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by chenyuan on 6/7/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context) {
        super(context, R.layout.move_grid_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.move_grid_item, parent, false);
        }
        Movie movie = getItem(position);
        Picasso.with(getContext()).load(Constant.IMG_BASE_URI + movie.getPosterPath()).into((ImageView) convertView);
        return convertView;
    }
}
