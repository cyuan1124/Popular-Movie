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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenyuan on 6/7/16.
 */
public class DetailFragment extends Fragment {

    @BindView(R.id.movie_title) TextView title;
    @BindView(R.id.vote_count) TextView voteCount;
    @BindView(R.id.ratings) TextView ratings;
    @BindView(R.id.movie_overview) TextView movieOverview;
    @BindView(R.id.movie_backdrop_image) ImageView backdropImageView;
    @BindView(R.id.movie_poster) ImageView moviePoster;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Movie movie = getActivity().getIntent().getParcelableExtra(Constant.Extras.Movie.name());
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        title.setText(String.format("%s(%s)", movie.getTitle(), movie.getReleaseDate().substring(0, 4)));
        voteCount.setText(getResources().getQuantityString(R.plurals.vote, movie.getVoteCount(), movie.getVoteCount()));
        ratings.setText(String.format(Locale.getDefault(), "%.1f/10", movie.getVoteAverage()));
        movieOverview.setText(movie.getOverview());
        backdropImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                backdropImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                backdropImageView.getLayoutParams().height = (int)(0.56f * backdropImageView.getWidth());
            }
        });
        Picasso.with(getActivity()).load(Constant.IMG_BASE_URI + movie.getPosterPath()).into(moviePoster);
        Picasso.with(getActivity()).load(Constant.BACKDROP_IMG_BASE_URI + movie.getBackdropPath()).into(backdropImageView);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }
}
