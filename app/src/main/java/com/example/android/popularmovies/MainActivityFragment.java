package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
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
    private WeakReference<FetchMoviesTask> asyncTaskWeakRef;
    private static final int FAVORITE_LOADER = 0;
    private static final String KEY_MOVIES = "moviesList";
    Boolean isConnected;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private final String DETAIL_FRAGMENT_TAG = "DFTAG";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sort = sharedPref.getString(getResources().getString(R.string.sort_by_key),
                getResources().getString(R.string.sort_popular));
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
            if (movies !=null)
            updateAdapter(movies);
                 }else{
            checkConnection();
            if (isConnected){
                //fetchMovies(getResources().getString(R.string.sort_popular));
                if (sort.equals(getResources().getString(R.string.sort_favorites))) {
                    getLoaderManager().restartLoader(FAVORITE_LOADER, null, this);
                }else {
                    fetchMovies(sort);
                }
            } else{
                Toast.makeText(getContext(), R.string.no_connection_message,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity())
                        .onItemSelected(movieAdapter.getItem(position));
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
        editor = sharedPref.edit();
        if (getActivity().findViewById(R.id.movie_detail_container) != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new MovieDetailsFragment(), DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        if (id == R.id.action_popular) {
            editor.putString(getResources().getString(R.string.sort_by_key),getResources().getString(R.string.sort_popular) );
            editor.commit();
            fetchMovies(getResources().getString(R.string.sort_popular));
        }
        if (id == R.id.action_rated) {
            editor.putString(getResources().getString(R.string.sort_by_key),getResources().getString(R.string.sort_top_rated) );
            editor.commit();
            fetchMovies(getResources().getString(R.string.sort_top_rated));
        }
        if (id == R.id.action_favorites) {
            editor.putString(getResources().getString(R.string.sort_by_key),getResources().getString(R.string.sort_favorites) );
            editor.commit();
            getLoaderManager().restartLoader(FAVORITE_LOADER, null, this);
        }
        return super.onOptionsItemSelected(item);
    }

    public interface Callback<Movie>{
        void onItemSelected(Movie movieUri);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), FavoritesProvider.Favorites.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            movies = new ArrayList<>(cursor.getCount());
            Log.d("Cursor", String.valueOf(cursor.getCount()));
            while (cursor.moveToNext()) {
                Movie movie = new Movie(cursor.getString(cursor.getColumnIndex(FavoritesColumns.TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.RELEASE_DATA)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.VOTE_AVERAGE)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(FavoritesColumns.MOVIE_ID)));
                movies.add(movie);
            }
            cursor.close();
            updateAdapter(movies);
        }else{
            Toast.makeText(getContext(), R.string.no_favorites_message,Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

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

            movies = new ArrayList<>(moviesArray.length());
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJ = moviesArray.getJSONObject(i);

                String moviePoster = movieJ.getString(POSTER_PATH);
                String movieTitle = movieJ.getString(ORIGINAL_TITLE);
                String releaseDate = movieJ.getString(RELEASE_DATE);
                String overView = movieJ.getString(OVERVIEW);
                String voteAverage = movieJ.getString(VOTE_AVERAGE);
                String url = poster_url.concat(moviePoster);
                String movieID = movieJ.getString(MOVIE_ID);

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
                //final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
               // final String QUERY_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        //.appendQueryParameter(QUERY_PARAM, params[0])
                        .appendPath(params[0])
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
                updateAdapter(strings);
                            }
        }
    }
  }
    // Utilites **********************************/
    private void updateAdapter(List<Movie> movieToAdd){
        movieAdapter.clear();
        movieAdapter.addAll(movieToAdd);
        movieAdapter.notifyDataSetChanged();
    }

    private void fetchMovies(String sortByString){
        FetchMoviesTask weatherTask = new FetchMoviesTask(this);
        this.asyncTaskWeakRef = new WeakReference<FetchMoviesTask>(weatherTask);
        weatherTask.execute(sortByString);
    }

    private void checkConnection(){
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
  }
