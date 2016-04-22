package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoritesColumns;
import com.example.android.popularmovies.data.FavoritesProvider;

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

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
   private MovieAdapter movieAdapter;
    private ArrayList<Movie> movies;
   // Movie[] movies;
    private WeakReference<FetchMoviesTask> asyncTaskWeakRef;
    private static final int FAVORITE_LOADER = 0;
    private static final String KEY_MOVIES = "moviesList";
    Boolean isConnected;

    public MainActivityFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
       // setRetainInstance(true);

        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
           // movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
          //  movieAdapter.clear();
            movieAdapter.addAll(movies);
          //  movieAdapter.notifyDataSetChanged();
        }else{
            if (isConnected){
                               // noConnection.setVisibility(View.GONE);
               // movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
                updateMovies();
            } else{
                Toast.makeText(getContext(),"No Connection",Toast.LENGTH_SHORT).show();
                // noConnection.setVisibility(View.VISIBLE);
            }
        }
       }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //movieAdapter = new MovieAdapter(getActivity(), new ArrayList(Arrays.asList(movies)));
        /// movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie flavorClick = movieAdapter.getItem(position);
                Intent movieClick = new Intent(getActivity(),MovieDetails.class);
                movieClick.putExtra("movie",flavorClick);
                startActivity(movieClick);
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MOVIES,movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
       // return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getContext(), SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_favorites) {
          //  Toast.makeText(getContext(),"Fav",Toast.LENGTH_SHORT).show();
            getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isAsyncTaskPendingOrRunning() {
        return this.asyncTaskWeakRef != null &&
                this.asyncTaskWeakRef.get() != null &&
                !this.asyncTaskWeakRef.get().getStatus().equals(AsyncTask.Status.FINISHED);
    }
    private void updateMovies(){
        FetchMoviesTask weatherTask = new FetchMoviesTask(this);
        this.asyncTaskWeakRef = new WeakReference<FetchMoviesTask>(weatherTask);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(getString(R.string.sort_by_key),
                getString(R.string.pref_unit_value));
        weatherTask.execute(sort_by);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), FavoritesProvider.Favorites.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            //cursor.moveToFirst();
           // movies = new Movie[cursor.getCount()];
            movies = new ArrayList<>(cursor.getCount());
            Log.d("Cursor", String.valueOf(cursor.getCount()));
          //  for (int i = 0; i < cursor.getCount(); i++) {
            while (cursor.moveToNext()) {
              // movies[i] = new Movie(cursor.getString(cursor.getColumnIndex(FavoritesColumns.TITLE)),
                Movie movie = new Movie(cursor.getString(cursor.getColumnIndex(FavoritesColumns.TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.RELEASE_DATA)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.VOTE_AVERAGE)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.MOVIE_ID)));
               // cursor.moveToNext();
                movies.add(movie);

            }

            cursor.close();
           // movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
            movieAdapter.clear();
            movieAdapter.addAll(movies);
            movieAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getContext(),"No FAvs",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private class FetchMoviesTask extends AsyncTask<String,Void,List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private WeakReference<MainActivityFragment> fragmentWeakRef;

        private FetchMoviesTask (MainActivityFragment fragment) {
            this.fragmentWeakRef = new WeakReference<MainActivityFragment>(fragment);
        }


        private List<Movie> getMovieDataFromJson(String forecastJsonStr)
            throws JSONException{
            // These are the names of the JSON objects that need to be extracted.
            final String MOVIES_RESULT = "results";
            final String POSTER_PATH = "poster_path";
            final String ORIGINAL_TITLE = "original_title";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String VOTE_AVERAGE = "vote_average";
            final String poster_url = "http://image.tmdb.org/t/p/w185/";
            final String MOVIE_ID = "id";

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_RESULT);

           // movies = new Movie[moviesArray.length()];
           movies = new ArrayList<>(moviesArray.length());
            for (int i = 0; i < moviesArray.length(); i++) {
                                // Get the JSON object representing the movie
                JSONObject movieJ = moviesArray.getJSONObject(i);
                String moviePoster = movieJ.getString(POSTER_PATH);
                String movieTitle = movieJ.getString(ORIGINAL_TITLE);
                String releaseDate = movieJ.getString(RELEASE_DATE);
                String overView = movieJ.getString(OVERVIEW);
                String voteAverage = movieJ.getString(VOTE_AVERAGE);
                String url = poster_url.concat(moviePoster);
                String movieID = movieJ.getString(MOVIE_ID);
                                  //movieAdapter.add(new Movie(url));
               // movies[i] = new Movie(movieTitle,overView,releaseDate,voteAverage, url,movieID);
                Movie movie = new Movie(movieTitle,overView,releaseDate,voteAverage, url,movieID);
                movies.add(movie);
            }
            return  movies;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJasonStr = null;
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.API_KEY)
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
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> strings) {
            if (this.fragmentWeakRef.get() != null) {
            if (strings != null) {
                //movieAdapter.add(new Movie(s));
                movieAdapter.clear();
                movieAdapter.addAll(strings);
                movieAdapter.notifyDataSetChanged();
            }
        }
    }
  }
}
