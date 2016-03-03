package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

   private AndroidFlavorAdapter flavorAdapter;
    private ArrayAdapter<String> mAdapter;
    AndroidFlavor[] androidFlavors;
   // private ArrayList<AndroidFlavor> flavorList;

    @Override
    public void onStart() {
        super.onStart();
        new FetchWeatherTask().execute();
   }
   /* @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("flavors")) {
            flavorList = new ArrayList<AndroidFlavor>(Arrays.asList(androidFlavors));
        }
        else {
            flavorList = savedInstanceState.getParcelableArrayList("flavors");
        }
    }

    public MainActivityFragment() {
    }

 /*   @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("flavors", flavorList);
        super.onSaveInstanceState(outState);
    }*/

  /*  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        flavorAdapter = new AndroidFlavorAdapter(getActivity(), flavorList);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_flavor);
        listView.setAdapter(flavorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AndroidFlavor flavorClick = flavorAdapter.getItem(i);
                flavorClick.versionName += ":)";
                flavorAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }*/

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

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
            new FetchWeatherTask().execute();
        }

        return super.onOptionsItemSelected(item);
    }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_main, container, false);

      //flavorAdapter = new AndroidFlavorAdapter(getActivity(), new ArrayList(Arrays.asList(androidFlavors)));

      flavorAdapter = new AndroidFlavorAdapter(getActivity(), new ArrayList<AndroidFlavor>());
      // Get a reference to the ListView, and attach this adapter to it.
      GridView gridView = (GridView) rootView.findViewById(R.id.flavors_grid);
      gridView.setAdapter(flavorAdapter);

      return rootView;
  }




    private class FetchWeatherTask extends AsyncTask<String,Void,AndroidFlavor[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

       // private String[] getWeatherDataFromJson(String forecastJsonStr)
            //throws JSONException {
       private AndroidFlavor[] getWeatherDataFromJson(String forecastJsonStr)
            throws JSONException{
            // These are the names of the JSON objects that need to be extracted.
            final String MOVIES_RESULT = "results";
            final String POSTER_PATH = "poster_path";
            final String TITLE = "title";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";
            final String OWM_DATETIME = "dt";

            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_RESULT);

        /*    SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));*/

            String[] resultStrs = new String[moviesArray.length()];
            androidFlavors = new AndroidFlavor[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePoster = movie.getString(POSTER_PATH);
                String movieTitle = movie.getString(TITLE);
                String url = "http://image.tmdb.org/t/p/w185/".concat(moviePoster);

                resultStrs[i] = url;

                    //flavorAdapter.add(new AndroidFlavor(url));
                androidFlavors[i] = new AndroidFlavor(movieTitle, url);
                Log.d(LOG_TAG, androidFlavors[i].toString());
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie: " + s);
            }
            return androidFlavors;// resultStrs;
        }

        @Override
        protected AndroidFlavor[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
//http://api.themoviedb.org/3/discover/movie?sort_by=highest-rated.desc&api_key=666d1649e381a40ffcfed1c252c74584
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by" +
                        "=popularity.desc&api_key=666d1649e381a40ffcfed1c252c74584");

                // Create the request to OpenWeatherMap, and open the connection
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
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
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
                return getWeatherDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AndroidFlavor[] strings) {
            if (strings != null){
               //flavorAdapter.add(new AndroidFlavor(s));
                flavorAdapter.addAll(strings);

        }
    }
  }
}
