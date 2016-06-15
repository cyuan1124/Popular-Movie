package com.chefmic.movie.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by chenyuan on 6/15/16.
 */
public class Utils {

    public static String getSortOrder(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_order_default));
    }

    public static boolean displayFavourite(Context context) {
        return context.getString(R.string.pref_sort_order_favourite).equals(getSortOrder(context));
    }

}
