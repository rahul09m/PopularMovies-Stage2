package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rmenezes on 3/1/2016.
 */
public class Movie implements Parcelable {
    String movieName;
    String overView;
    String releaseDate;
    String userRating;
    String image;
    String movieID;

    public Movie (String mName, String oView,String rDate, String userR, String image,String id) {
        this.movieName = mName;
        this.overView = oView;
        this.releaseDate = rDate;
        this.userRating = userR;
        this.image = image;
        this.movieID=id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieName);
        dest.writeString(this.overView);
        dest.writeString(this.releaseDate);
        dest.writeString(this.userRating);
        dest.writeString(this.image);
        dest.writeString(this.movieID);
    }

    protected Movie(Parcel in) {
        this.movieName = in.readString();
        this.overView = in.readString();
        this.releaseDate = in.readString();
        this.userRating = in.readString();
        this.image = in.readString();
        this.movieID = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
