package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoritesColumns;
import com.example.android.popularmovies.data.FavoritesProvider;
import com.squareup.picasso.Picasso;


public class MovieDetailsFragment extends Fragment {
    // FloatingActionButton fabFavorite;
    Movie myMovie;
    Context mContext;
    FloatingActionButton fabFavorite;

    public MovieDetailsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent receiveIntent = getActivity().getIntent();
        myMovie = receiveIntent.getParcelableExtra("movie");
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

        return view;
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