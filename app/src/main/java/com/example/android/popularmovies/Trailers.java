package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rmenezes on 4/25/2016.
 */
public class Trailers implements Parcelable {
    String name;
    String size;
    String source;
    String type;

    public Trailers(String NAME,String SIZE,String SOURCE,String TYPE){
        this.name = NAME;
        this.size = SIZE;
        this.source = SOURCE;
        this.type = TYPE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.size);
        dest.writeString(this.source);
        dest.writeString(this.type);
    }

    protected Trailers(Parcel in) {
        this.name = in.readString();
        this.size = in.readString();
        this.source = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Trailers> CREATOR = new Parcelable.Creator<Trailers>() {
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
