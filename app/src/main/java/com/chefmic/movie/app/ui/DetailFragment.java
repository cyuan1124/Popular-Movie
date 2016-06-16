package com.chefmic.movie.app.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.chefmic.movie.app.Constant;
import com.chefmic.movie.app.MovieApplication;
import com.chefmic.movie.app.R;
import com.chefmic.movie.app.data.MovieContract;
import com.chefmic.movie.app.endpoint.FetchReviewsService;
import com.chefmic.movie.app.endpoint.FetchTrailersService;
import com.chefmic.movie.app.parcelable.Movie;
import com.chefmic.movie.app.parcelable.Review;
import com.chefmic.movie.app.parcelable.Reviews;
import com.chefmic.movie.app.parcelable.Trailer;
import com.chefmic.movie.app.parcelable.Trailers;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by chenyuan on 6/7/16.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;

    @BindView(R.id.movie_title)
    TextView title;
    @BindView(R.id.vote_count)
    TextView voteCount;
    @BindView(R.id.ratings)
    TextView ratings;
    @BindView(R.id.movie_overview)
    TextView movieOverview;
    @BindView(R.id.movie_backdrop_image)
    ImageView backdropImageView;
    @BindView(R.id.movie_poster)
    ImageView moviePoster;
    @BindView(R.id.trailer_container_view_stub)
    ViewStub trailersViewStub;
    @BindView(R.id.review_container_view_stub)
    ViewStub reviewsViewStub;
    @BindView(R.id.add_fav_toggle)
    ToggleButton addFavToggleButton;

    private Subscription trailersSubscription, reviewsSubscription;
    private Movie movie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (getActivity() instanceof DetailActivity) {
            movie = getActivity().getIntent().getParcelableExtra(Constant.Extras.Movie.name());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            movie = getArguments().getParcelable(Constant.Extras.Movie.name());
            toolbar.setVisibility(View.GONE);
        }
        title.setText(String.format("%s(%s)", movie.getTitle(), movie.getReleaseDate().substring(0, 4)));
        voteCount.setText(getResources().getQuantityString(R.plurals.vote, movie.getVoteCount(), movie.getVoteCount()));
        ratings.setText(String.format(Locale.getDefault(), "%.1f/10", movie.getVoteAverage()));
        movieOverview.setText(movie.getOverview());
        Picasso.with(getActivity()).load(Constant.IMG_BASE_URI + movie.getPosterPath()).into(moviePoster);
        Picasso.with(getActivity()).load(Constant.BACKDROP_IMG_BASE_URI + movie.getBackdropPath()).into(backdropImageView);
        FetchTrailersService fetchTrailersService = MovieApplication.retrofit.create(FetchTrailersService.class);
        trailersSubscription = fetchTrailersService.getTrailerResult(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Trailers>() {
                    @Override
                    public void call(Trailers trailers) {
                        if (trailers == null || trailers.getResults() == null || trailers.getResults().isEmpty()) {
                            return;
                        }
                        LinearLayout trailerContainer = (LinearLayout) trailersViewStub.inflate();
                        LinearLayout trailerList = (LinearLayout) trailerContainer.findViewById(R.id.trailers);
                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                        for (Trailer trailer : trailers.getResults()) {
                            trailerList.addView(createViewForTrailer(layoutInflater, trailerList, trailer));
                        }
                    }
                });

        reviewsSubscription = MovieApplication.retrofit.create(FetchReviewsService.class)
                .getReviewResult(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Reviews>() {
                    @Override
                    public void call(Reviews reviews) {
                        if (reviews == null || reviews.getResults() == null || reviews.getResults().isEmpty()) {
                            return;
                        }
                        LinearLayout reviewContainer = (LinearLayout) reviewsViewStub.inflate();
                        LinearLayout reviewList = (LinearLayout) reviewContainer.findViewById(R.id.reviews);
                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                        for (Review review : reviews.getResults()) {
                            reviewList.addView(createViewForReview(layoutInflater, reviewList, review));
                        }
                    }
                });

        addFavToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isChecked = addFavToggleButton.isChecked();
                if (isChecked) {
                    addToFav();
                } else {
                    removeFromFav();
                }
                final Snackbar snackbar = Snackbar.make(view,
                        isChecked ? R.string.movie_added_to_fav : R.string.movie_removed_from_fav,
                        Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isChecked) {
                            removeFromFav();
                        } else {
                            addToFav();
                        }
                        addFavToggleButton.setChecked(!isChecked);
                    }
                });
                snackbar.show();
            }
        });
        return view;
    }

    protected void finish() {
        String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_order_default));
        if (sortOrder.equals(getString(R.string.pref_sort_order_favourite)) && !addFavToggleButton.isChecked()) {
            getActivity().finish();
        } else {
            getActivity().supportFinishAfterTransition();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.buildMovieUri(movie.getId());
        return new CursorLoader(getActivity(),
                uri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        addFavToggleButton.setChecked(data.getCount() != 0);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void addToFav() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).reloadData();
        }
    }

    private void removeFromFav() {
        getContext().getContentResolver().delete(MovieContract.MovieEntry.buildMovieUri(movie.getId()), null, null);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).reloadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (trailersSubscription != null && !trailersSubscription.isUnsubscribed()) {
            trailersSubscription.unsubscribe();
        }
        if (reviewsSubscription != null && !reviewsSubscription.isUnsubscribed()) {
            reviewsSubscription.unsubscribe();
        }
    }

    private View createViewForTrailer(LayoutInflater layoutInflater, ViewGroup parent, final Trailer trailer) {
        View view = layoutInflater.inflate(R.layout.trailer_cell, parent, false);
        ThumbnailViewHolder viewHolder = new ThumbnailViewHolder(view);
        Picasso.with(getActivity())
                .load(String.format(Constant.YOUTUBE_THUMBNAIL_URI, trailer.getKey()))
                .into(viewHolder.thumbnail);
        viewHolder.title.setText(trailer.getName());
        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/v/" + trailer.getKey())));
            }
        });
        return view;
    }

    private View createViewForReview(LayoutInflater layoutInflater, ViewGroup parent, final Review review) {
        View view = layoutInflater.inflate(R.layout.review_cell, parent, false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        viewHolder.author.setText(getString(R.string.movie_review_author, review.getAuthor()));
        viewHolder.content.setText(review.getContent());
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl())));
            }
        });
        return view;
    }

    static class ThumbnailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trailer_thumbnail)
        ImageView thumbnail;
        @BindView(R.id.trailer_title)
        TextView title;

        ThumbnailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_author)
        TextView author;
        @BindView(R.id.review_content)
        TextView content;

        ReviewViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
