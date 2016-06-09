package com.chefmic.movie.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.chefmic.movie.app.Constant;
import com.chefmic.movie.app.R;
import com.chefmic.movie.app.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * Created by chenyuan on 6/7/16.
 */
public class DetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Movie movie = getActivity().getIntent().getParcelableExtra(Constant.Extras.Movie.name());
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ((TextView) view.findViewById(R.id.movie_title)).setText(String.format("%s(%s)", movie.getTitle(), movie.getReleaseDate().substring(0, 4)));
        ((TextView) view.findViewById(R.id.vote_count)).setText(getResources().getQuantityString(R.plurals.vote, movie.getVoteCount(), movie.getVoteCount()));
        ((TextView) view.findViewById(R.id.ratings)).setText(String.format(Locale.getDefault(), "%.1f/10", movie.getVoteAverage()));
        ((TextView) view.findViewById(R.id.movie_overview)).setText(movie.getOverview());
        final ImageView backdropImage = (ImageView) view.findViewById(R.id.movie_backdrop_image);
        backdropImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                backdropImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                backdropImage.getLayoutParams().height = (int)(0.56f * backdropImage.getWidth());
            }
        });
        Picasso.with(getActivity()).load(Constant.IMG_BASE_URI + movie.getPosterPath()).into((ImageView) view.findViewById(R.id.movie_poster));
        Picasso.with(getActivity()).load(Constant.BACKDROP_IMG_BASE_URI + movie.getBackdropPath()).into((ImageView) view.findViewById(R.id.movie_backdrop_image));
    Log.i("MovieImage", "" + Constant.BACKDROP_IMG_BASE_URI + movie.getBackdropPath());
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }
}
