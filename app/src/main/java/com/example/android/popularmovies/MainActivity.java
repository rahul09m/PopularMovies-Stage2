package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback<Movie>{

    Boolean mTwoPane;
    private static final String MOVIE_TAG = "movie";
    private final String DETAIL_FRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailsFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Movie movieUri) {
        if (mTwoPane) {
            // In two-pane mode, callback from Fragment
            Bundle args = new Bundle();
            args.putParcelable(MOVIE_TAG,movieUri);
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(args);

           getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent movieClick = new Intent(this, MovieDetails.class);
            movieClick.putExtra(MOVIE_TAG, movieUri);
            startActivity(movieClick);
        }
    }
}
