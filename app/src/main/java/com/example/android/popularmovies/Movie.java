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
    String image; // drawable reference id



    public Movie (String mName, String oView,String rDate, String userR, String image) {
        this.movieName = mName;
        this.overView = oView;
        this.releaseDate = rDate;
        this.userRating = userR;
        this.image = image;
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
    }

    protected Movie(Parcel in) {
        this.movieName = in.readString();
        this.overView = in.readString();
        this.releaseDate = in.readString();
        this.userRating = in.readString();
        this.image = in.readString();
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
    /*

    private Movie(Parcel in){
        movieName = in.readString();
        //versionNumber = in.readString();
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return movieName  + "--" + image; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieName);
       // parcel.writeString(versionNumber);
        parcel.writeString(image);
    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
  /*  public Movie(JSONObject object){
        try {
            this.name = object.getString("name");
            this.hometown = object.getString("hometown");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
  /*  public static ArrayList<User> fromJson(JSONArray jsonObjects) {
           ArrayList<User> users = new ArrayList<User>();
           for (int i = 0; i < jsonObjects.length(); i++) {
               try {
                  users.add(new User(jsonObjects.getJSONObject(i)));
               } catch (JSONException e) {
                  e.printStackTrace();
               }
          }
          return users;
    }
}*/