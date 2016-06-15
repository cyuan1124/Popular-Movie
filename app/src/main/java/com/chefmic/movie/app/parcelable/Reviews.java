package com.chefmic.movie.app.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by chenyuan on 6/14/16.
 */
public class Reviews implements Parcelable {

    private final Long id;
    private final ArrayList<Review> results;
    @SerializedName("total_pages")
    private final Integer totalPages;
    @SerializedName("total_results")
    private final Integer totalResults;

    public Long getId() {
        return id;
    }

    public ArrayList<Review> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeTypedList(this.results);
        dest.writeValue(this.totalPages);
        dest.writeValue(this.totalResults);
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    protected Reviews(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.results = in.createTypedArrayList(Review.CREATOR);
        this.totalPages = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalResults = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel source) {
            return new Reviews(source);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };
}
