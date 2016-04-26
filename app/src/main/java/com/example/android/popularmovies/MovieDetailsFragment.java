package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    FloatingActionButton fabFavorite;
    private ArrayList<Reviews> reviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private ListView listviewReviews;
    private ListView listviewTrailers;
    private ArrayList trailers;
    private static final String MOVIE_TAG = "movie";
    Boolean isConnected;
    String movie_ID;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null){
            myMovie = arguments.getParcelable(MOVIE_TAG);
        }
        if (myMovie != null) {
            Log.d("myMovieinMovieDFragment", String.valueOf(myMovie));
            // myMovie = receiveIntent.getParcelableExtra(MOVIE_TAG);
            //  }
            reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Reviews>());
            trailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailers>());
            checkConnection();
            if (isConnected) {
                FetchTrailersandReviews fetchTrailersandReviews = new FetchTrailersandReviews(this);
                fetchTrailersandReviews.execute(myMovie.movieID);
            } else {
                Toast.makeText(getContext(), R.string.no_connection_message, Toast.LENGTH_SHORT).show();
            }
            //setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (myMovie != null) {
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
            Button reviewButton = (Button) view.findViewById(R.id.review_button);
            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!reviews.isEmpty()) {
                        Log.d("BUTTONREVIEWS", String.valueOf(reviews));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reviewAdapter.clear();
                                reviewAdapter.addAll(reviews);
                                reviewAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "No Reviews", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            listviewTrailers = (ListView) view.findViewById(R.id.listview_trailer);
            listviewTrailers.setAdapter(trailerAdapter);
            Button trailerButton = (Button) view.findViewById(R.id.trailer_button);
            trailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!trailers.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trailerAdapter.clear();
                                trailerAdapter.addAll(trailers);
                                trailerAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "No Trailers", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return view;
        }
        return null;
    }

    private class FetchTrailersandReviews extends AsyncTask<String,Void,Void> {

        private final String LOG_TAG = FetchTrailersandReviews.class.getSimpleName();
        private WeakReference<MovieDetailsFragment> fragmentWeakRef;

        private FetchTrailersandReviews(MovieDetailsFragment fragment) {
            this.fragmentWeakRef = new WeakReference<MovieDetailsFragment>(fragment);
        }

        private List getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {

            final String REVIEWS = "reviews";
            final String REVIEWS_RESULT = "results";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String REVIEWURL = "url";
            final String REVIEW_ID = "id";

            final String TRAILERS = "trailers";
            final String YOUTUBE = "youtube";
            final String TRAILER_NAME = "name";
            final String TRAILER_SIZE= "size";
            final String TRAILER_SOURCE ="source";
            final String TRAILER_TYPE = "type";

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            //Reviews
            JSONObject reviewsJSON = moviesJson.getJSONObject(REVIEWS);
            JSONArray moviesArrayReviews = reviewsJSON.getJSONArray(REVIEWS_RESULT);

            reviews = new ArrayList<>(moviesArrayReviews.length());
            for (int i = 0; i < moviesArrayReviews.length(); i++) {
                JSONObject reviewsJ = moviesArrayReviews.getJSONObject(i);

                String reviewID = reviewsJ.getString(REVIEW_ID);
                String author = reviewsJ.getString(AUTHOR);
                String content = reviewsJ.getString(CONTENT);
                String url = reviewsJ.getString(REVIEWURL);

                Reviews review = new Reviews(reviewID,author,content,url);
                reviews.add(review);
            }
            //Trailers
            JSONObject trailersJSON = moviesJson.getJSONObject(TRAILERS);
            JSONArray moviesArrayTrailers = trailersJSON.getJSONArray(YOUTUBE);

            trailers = new ArrayList<>(moviesArrayTrailers.length());
            for (int i = 0; i < moviesArrayTrailers.length(); i++) {
                JSONObject trailersJ = moviesArrayTrailers.getJSONObject(i);

                String trailerName = trailersJ.getString(TRAILER_NAME);
                String size = trailersJ.getString(TRAILER_SIZE);
                String source = trailersJ.getString(TRAILER_SOURCE);
                String type = trailersJ.getString(TRAILER_TYPE);

                Trailers trailer = new Trailers(trailerName,size,source,type);
                trailers.add(trailer);
            }
            return null;
        }

        @Override
        protected Void doInBackground(String... params) {
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
                getMovieDataFromJson(movieJasonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            Log.d(LOG_TAG,"JSON STUFF: " +movieJasonStr);
            return null;
        }

       /* @Override
        protected void onPostExecute(List strings) {
            super.onPostExecute(strings);
            if (this.fragmentWeakRef.get() != null) {
                if (strings != null) {
                    reviewAdapter.addAll(strings);
                }
            }
        }*/
    }

    private void addToFavorite() {
        if (isFavorite()){
            Toast.makeText(getContext(), R.string.movie_favorite_delete_message ,Toast.LENGTH_LONG).show();
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
            Toast.makeText(getContext(), R.string.movie_favorite_add_message,Toast.LENGTH_LONG).show();
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

    private void checkConnection(){
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}