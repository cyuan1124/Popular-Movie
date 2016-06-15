package com.chefmic.movie.app.ui.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.chefmic.movie.app.Constant;
import com.chefmic.movie.app.R;
import com.chefmic.movie.app.parcelable.Movie;
import com.chefmic.movie.app.ui.MoviesFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by chenyuan on 6/15/16.
 */
public class MovieCursorAdapter extends CursorAdapter {

    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.move_grid_item, parent, false);
    }

    public Movie getItem(int position) {
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            return loadMovieFromCursor(cursor);
        }
        return null;
    }

    private Movie loadMovieFromCursor(Cursor cursor) {
        Long id = cursor.getLong(MoviesFragment.COL_MOVIE_ID);
        String posterPath = cursor.getString(MoviesFragment.COL_POSTER_PATH);
        String backdropPath = cursor.getString(MoviesFragment.COL_BACKDROP_URL);
        String overview = cursor.getString(MoviesFragment.COL_OVERVIEW);
        String title = cursor.getString(MoviesFragment.COL_TITLE);
        String releaseDate = cursor.getString(MoviesFragment.COL_RELEASE_DATE);
        Integer voteCount = cursor.getInt(MoviesFragment.COL_VOTE_COUNT);
        Double voteAverage = cursor.getDouble(MoviesFragment.COL_VOTE_AVERAGE);
        return new Movie(posterPath, null, overview, releaseDate, null, id, null, null, title,
                backdropPath, null, voteCount, null, voteAverage);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Movie movie = loadMovieFromCursor(cursor);
        Picasso.with(context).load(Constant.IMG_BASE_URI + movie.getPosterPath()).into((ImageView) view);
    }
}
