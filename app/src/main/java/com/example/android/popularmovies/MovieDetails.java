package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by Rahul on 04/03/2016.
 */
public class MovieDetails extends AppCompatActivity {
    private final String LOG_TAG = MovieDetails.class.getSimpleName();
    private static final String MOVIE_TAG = "movie";

    Movie myMovie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        Movie movieId = null;
        if (extras != null) {
            movieId = extras.getParcelable(MOVIE_TAG);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MOVIE_TAG, movieId);

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
            }
        return super.onOptionsItemSelected(item);
    }
}

