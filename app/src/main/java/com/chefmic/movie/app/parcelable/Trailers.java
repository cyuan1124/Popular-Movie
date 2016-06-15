package com.chefmic.movie.app.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by chenyuan on 6/12/16.
 */
public class Trailers implements Parcelable {

    private final Long id;
    private final ArrayList<Trailer> results;

    public Long getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeTypedList(this.results);
    }

    public ArrayList<Trailer> getResults() {
        return results;
    }

    protected Trailers(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.results = in.createTypedArrayList(Trailer.CREATOR);
    }

    public static final Creator<Trailers> CREATOR = new Creator<Trailers>() {
        @Override
        public Trailers createFromParcel(Parcel source) {
            return new Trailers(source);
        }

        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };
}
