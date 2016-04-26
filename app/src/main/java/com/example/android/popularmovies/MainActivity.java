package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback<Movie>{
   // private RetainedFragment dataFragment;
  Boolean mTwoPane;
    private static final String MOVIE_TAG = "movie";
    Movie myMovie;
    private final String DETAIL_FRAGMENT_TAG = "DFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.movie_detail_container) != null) {
            Log.d("MainActivi","in here");
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

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }*/

       /* if (id == R.id.action_favorites) {
            Toast.makeText(getApplicationContext(),"Fav",Toast.LENGTH_SHORT).show();
        }*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Movie movieUri) {
        if (mTwoPane) {
            Log.d("TWOPANE","Here");
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
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
            Log.d("myMovieinMsin",String.valueOf(movieUri));
            startActivity(movieClick);
        }
    }
}
