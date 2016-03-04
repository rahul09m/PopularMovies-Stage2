package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Rahul on 04/03/2016.
 */
public class MovieDetails extends AppCompatActivity {
    private final String LOG_TAG = MovieDetails.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moviedetails_activity);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);*/

        Intent receiveIntent = getIntent();
        Movie myMovie =  receiveIntent.getParcelableExtra("movie");
       // String movieName= receiveIntent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d(LOG_TAG,"Moee:"+ myMovie.movieName);

       // String movieName = myMovie.
       TextView movieNameText = (TextView) findViewById(R.id.moviename);
        movieNameText.setText(myMovie.movieName);
        ImageView imageMovie = (ImageView) findViewById(R.id.movieimage);
        Picasso.with(getApplicationContext())
                .load(myMovie.image)
                .into(imageMovie);
        TextView releaseText = (TextView)findViewById(R.id.releasedate);
        releaseText.setText(myMovie.releaseDate);
        TextView overviewText = (TextView)findViewById(R.id.overview);
        overviewText.setText(myMovie.overView);

        TextView voteaverageText = (TextView)findViewById(R.id.voteaverage);
        voteaverageText.setText(myMovie.userRating);


    }


}
