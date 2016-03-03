package com.example.android.popularmovies;

/**
 * Created by rmenezes on 3/1/2016.
 */
public class AndroidFlavor {
    String versionName;
    String versionNumber;
    String image; // drawable reference id

    //public AndroidFlavor(String vName, String vNumber, String image)
    public AndroidFlavor(String vName, String image)
    {
        this.versionName = vName;
        //this.versionNumber = vNumber;
        this.image = image;
    }

  /*  public AndroidFlavor(JSONObject object){
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

    /*private AndroidFlavor(Parcel in){
        versionName = in.readString();
        versionNumber = in.readString();
        image = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return versionName + "--" + versionNumber + "--" + image; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(versionName);
        parcel.writeString(versionNumber);
        parcel.writeInt(image);
    }

    public final Parcelable.Creator<AndroidFlavor> CREATOR = new Parcelable.Creator<AndroidFlavor>() {
        @Override
        public AndroidFlavor createFromParcel(Parcel parcel) {
            return new AndroidFlavor(parcel);
        }

        @Override
        public AndroidFlavor[] newArray(int i) {
            return new AndroidFlavor[i];
        }

    };*/
}