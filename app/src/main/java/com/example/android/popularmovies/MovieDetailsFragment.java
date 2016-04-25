package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoritesColumns;
import com.example.android.popularmovies.data.FavoritesProvider;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MovieDetailsFragment extends Fragment {
    // FloatingActionButton fabFavorite;
    Movie myMovie;
    Context mContext;
    FloatingActionButton fabFavorite;
    private ArrayList<Reviews> reviews;
    private ReviewAdapter reviewAdapter;
    private ListView listviewReviews;
   private ArrayList trailers;

    public MovieDetailsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent receiveIntent = getActivity().getIntent();
        myMovie = receiveIntent.getParcelableExtra("movie");
        reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Reviews>());
        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(this);
        fetchReviewsTask.execute(myMovie.movieID);
        //setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        TextView movieNameText = (TextView) view.findViewById(R.id.moviename);
        movieNameText.setText(myMovie.movieName);
        ImageView imageMovie = (ImageView) view.findViewById(R.id.movieimage);
        Picasso.with(getContext())
                .load(myMovie.image)
                .error(R.drawable.ic_launcher)
                .placeholder(R.drawable.ic_launcher)
                .into(imageMovie);
        TextView releaseText = (TextView) view.findViewById(R.id.releasedate);
        releaseText.setText(myMovie.releaseDate);
        TextView overviewText = (TextView) view.findViewById(R.id.overview);
        overviewText.setText(myMovie.overView);

        TextView voteaverageText = (TextView) view.findViewById(R.id.voteaverage);
        voteaverageText.setText(myMovie.userRating);

        fabFavorite = (FloatingActionButton) view.findViewById(R.id.fab_add);
       if (isFavorite())
           fabFavorite.setImageResource(R.drawable.ic_star_black_24dp);

        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorite();
            }
        });


        listviewReviews = (ListView) view.findViewById(R.id.listview_review);
        listviewReviews.setAdapter(reviewAdapter);

        return view;
    }

    //private class FetchReviewsTask extends AsyncTask
    private class FetchReviewsTask extends AsyncTask<String,Void,List> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
        private WeakReference<MovieDetailsFragment> fragmentWeakRef;

        private FetchReviewsTask (MovieDetailsFragment fragment) {
            this.fragmentWeakRef = new WeakReference<MovieDetailsFragment>(fragment);
        }


        private List<Reviews> getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String REVIEWS_RESULT = "results";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String REVIEWURL = "url";
            final String REVIEW_ID = "id";

            final String TRAILER_NAME = "name";
            final String TRAILER_SIZE= "size";
            final String TRAILER_SOURCE ="source";
            final String TRAILER_TYPE = "type";

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONObject reviewsJSON = moviesJson.getJSONObject("reviews");
            JSONArray moviesArrayReviews = reviewsJSON.getJSONArray(REVIEWS_RESULT);

            // movies = new Movie[moviesArray.length()];
            reviews = new ArrayList<>(moviesArrayReviews.length());
            for (int i = 0; i < moviesArrayReviews.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject reviewsJ = moviesArrayReviews.getJSONObject(i);
                String reviewID = reviewsJ.getString(REVIEW_ID);
                String author = reviewsJ.getString(AUTHOR);
                String content = reviewsJ.getString(CONTENT);
                String url = reviewsJ.getString(REVIEWURL);

                Reviews review = new Reviews(reviewID,author,content,url);
                reviews.add(review);
            }
          //  Log.d(LOG_TAG,"reviews"+reviews);

            JSONObject trailersJSON = moviesJson.getJSONObject("trailers");
            JSONArray moviesArrayTrailers = trailersJSON.getJSONArray("youtube");

            trailers = new ArrayList<>(moviesArrayTrailers.length());
            for (int i = 0; i < moviesArrayTrailers.length(); i++) {
                JSONObject trailersJ = moviesArrayTrailers.getJSONObject(i);
                String trailerName = trailersJ.getString(TRAILER_NAME);
                String size = trailersJ.getString(TRAILER_SIZE);
                String source = trailersJ.getString(TRAILER_SOURCE);
                String type = trailersJ.getString(TRAILER_TYPE);

                Trailers trailer = new Trailers(trailerName,size,source,type);
                trailers.add(trailer);
                //Log.d(LOG_TAG,"trailers: "+ trailers);
            }
            return  reviews ;

        }

        @Override
        protected List doInBackground(String... params) {
            if (params.length==0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJasonStr = null;
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";
                final String DATA = "append_to_response";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.API_KEY)
                        .appendQueryParameter(DATA,"trailers,reviews")
                        .build();

                URL url= new URL(builtUri.toString());
                Log.d(LOG_TAG, "Built URI " + builtUri.toString());

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
                movieJasonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJasonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            Log.d(LOG_TAG,"JSON STUFF: " +movieJasonStr);
            return null;
        }

        @Override
        protected void onPostExecute(List strings) {
            super.onPostExecute(strings);
            if (this.fragmentWeakRef.get() != null) {
                if (strings != null) {
                    reviewAdapter.addAll(strings);
                }
            }
        }
    }

    private void addToFavorite() {
        if (isFavorite()){
            Toast.makeText(getContext(),"Movie Deleted",Toast.LENGTH_LONG).show();
            getContext().getContentResolver().delete(FavoritesProvider.Favorites.withMovieID(myMovie.movieID), null, null);
            fabFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
        }else {
            ContentValues cv = new ContentValues();
            cv.put(FavoritesColumns.TITLE, myMovie.movieName);
            cv.put(FavoritesColumns.RELEASE_DATA, myMovie.releaseDate);
            cv.put(FavoritesColumns.POSTER_PATH, myMovie.image);
            cv.put(FavoritesColumns.VOTE_AVERAGE, myMovie.userRating);
            cv.put(FavoritesColumns.OVERVIEW, myMovie.overView);
            cv.put(FavoritesColumns.MOVIE_ID, myMovie.movieID);
            Uri result = getContext().getContentResolver().insert(FavoritesProvider.Favorites.CONTENT_URI, cv);
            fabFavorite.setImageResource(R.drawable.ic_star_black_24dp);
            Toast.makeText(getContext(),"Movie Added",Toast.LENGTH_LONG).show();
        }
    }

    private boolean isFavorite(){
        Cursor c = getContext().getContentResolver().query(FavoritesProvider.Favorites.CONTENT_URI,
                new String[] {FavoritesColumns.MOVIE_ID},FavoritesColumns.MOVIE_ID+ "=?",
                new String[] {myMovie.movieID.toString()},null);
        if (c != null) {
            if (c.getCount() == 1){
                c.close();
                return true;
            }
            c.close();
        }
        return false;
    }
}