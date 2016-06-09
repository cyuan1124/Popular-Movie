package com.chefmic.movie.app.ui.widget;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.chefmic.movie.app.BuildConfig;
import com.chefmic.movie.app.Constant;
import com.chefmic.movie.app.data.Movie;
import com.chefmic.movie.app.data.MovieResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by chenyuan on 6/7/16.
 */
public class FetchMovieTask extends AsyncTask<Boolean, Void, MovieResult> {

    public interface Callback {
        void loaded(MovieResult movieResult);
    }

    private static final String TAG = "FetchMovie";

    private final Callback callback;

    public FetchMovieTask(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected MovieResult doInBackground(Boolean... params) {
        if (params.length == 0) {
            return null;
        }
        boolean loadPopular = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieResultJson = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String FORECAST_BASE_URL =
                    "http://api.themoviedb.org/3/movie";
            final String API_KEY = "api_key";
            final String POPULAR = "popular";
            final String TOP_RATED = "top_rated";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendPath(loadPopular ? POPULAR : TOP_RATED)
                    .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            Log.i(TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieResultJson = buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getMovieResultFromJson(movieResultJson);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(MovieResult result) {
        if (result != null && callback != null) {
            callback.loaded(result);
        }
    }

    private MovieResult getMovieResultFromJson(String json) throws JSONException {
        if (json == null) {
            return null;
        }
        JSONObject movieResultJson = new JSONObject(json);
        ArrayList<Movie> movies = getMovieListFromJson(movieResultJson.getJSONArray(Constant.MD_RESULTS));
        return new MovieResult(movieResultJson.getInt(Constant.MD_PAGE),
                movies,
                movieResultJson.getInt(Constant.MD_TOTAL_RESULTS),
                movieResultJson.getInt(Constant.MD_TOTAL_PAGES));
    }

    private ArrayList<Movie> getMovieListFromJson(JSONArray results) throws JSONException {
        if (results == null || results.length() == 0) {
            return null;
        }
        ArrayList<Movie> movies = new ArrayList<>(results.length());
        for (int i = 0; i < results.length(); i++) {
            JSONObject object = results.getJSONObject(i);
            Movie movie = new Movie(object.getString(Constant.MD_POSTER_PATH), //poster path
                    object.getBoolean(Constant.MD_ADULT),
                    object.getString(Constant.MD_OVERVIEW),
                    object.getString(Constant.MD_RELEASE_DATE),
                    getGenreIdsFromJson(object.getJSONArray(Constant.MD_GENRE_IDS)),
                    object.getLong(Constant.MD_ID),
                    object.getString(Constant.MD_ORIGINAL_TITLE),
                    object.getString(Constant.MD_ORIGINAL_LANGUAGE),
                    object.getString(Constant.MD_TITLE),
                    object.getString(Constant.MD_BACKDROP_PATH),
                    object.getDouble(Constant.MD_POPULARITY),
                    object.getInt(Constant.MD_VOTE_COUNT),
                    object.getBoolean(Constant.MD_VIDEO),
                    object.getDouble(Constant.MD_VOTE_AVERAGE));
            movies.add(movie);
        }
        return movies;
    }

    private ArrayList<Integer> getGenreIdsFromJson(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        ArrayList<Integer> ids = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            ids.add(jsonArray.getInt(i));
        }
        return ids;
    }

}
