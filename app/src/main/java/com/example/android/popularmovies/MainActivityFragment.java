package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {
   private MovieAdapter movieAdapter;
    //private ArrayList<Movie> movies;
    Movie[] movies;

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
   }

    public MainActivityFragment() {
    }

 /*  @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((savedInstanceState == null) || !savedInstanceState.containsKey("moviesList")){
            movies = new Movie[0];
        }else {
            movies = savedInstanceState.getParcelable("moviesList");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("moviesList",movies);
        super.onSaveInstanceState(outState);
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            new FetchMoviesTask().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies(){
        FetchMoviesTask weatherTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(getString(R.string.sort_by_key),
                getString(R.string.pref_unit_value));
        weatherTask.execute(sort_by);
    }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_main, container, false);
      //movieAdapter = new MovieAdapter(getActivity(), new ArrayList(Arrays.asList(movies)));
      movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
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


    private class FetchMoviesTask extends AsyncTask<String,Void,Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

       private Movie[] getMovieDataFromJson(String forecastJsonStr)
            throws JSONException{
            // These are the names of the JSON objects that need to be extracted.
            final String MOVIES_RESULT = "results";
            final String POSTER_PATH = "poster_path";
            final String ORIGINAL_TITLE = "original_title";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String VOTE_AVERAGE = "vote_average";
            final String poster_url = "http://image.tmdb.org/t/p/w185/";

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_RESULT);

            movies = new Movie[moviesArray.length()];
          // movies = new ArrayList<Movie>(moviesArray.length());
            for (int i = 0; i < moviesArray.length(); i++) {
                                // Get the JSON object representing the movie
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePoster = movie.getString(POSTER_PATH);
                String movieTitle = movie.getString(ORIGINAL_TITLE);
                String releaseDate = movie.getString(RELEASE_DATE);
                String overView = movie.getString(OVERVIEW);
                String voteAverage = movie.getString(VOTE_AVERAGE);
                String url = poster_url.concat(moviePoster);
                                  //movieAdapter.add(new Movie(url));
                movies[i] = new Movie(movieTitle,overView,releaseDate,voteAverage, url);
            }
            return movies;
        }

        @Override
        protected Movie[] doInBackground(String... params) {
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
        protected void onPostExecute(Movie[] strings) {
            if (strings != null){
               //movieAdapter.add(new Movie(s));
                movieAdapter.clear();
                movieAdapter.addAll(strings);

        }
    }
  }
}
