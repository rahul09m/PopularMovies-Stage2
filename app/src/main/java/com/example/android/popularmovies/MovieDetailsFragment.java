package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
           }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        Intent receiveIntent = getActivity().getIntent();
        Movie myMovie =  receiveIntent.getParcelableExtra("movie");
        // String movieName= receiveIntent.getStringExtra(Intent.EXTRA_TEXT);
        //Log.d(LOG_TAG,"Moee:"+ myMovie.movieName);

        // String movieName = myMovie.
        TextView movieNameText = (TextView) view.findViewById(R.id.moviename);
        movieNameText.setText(myMovie.movieName);
        ImageView imageMovie = (ImageView) view.findViewById(R.id.movieimage);
        Picasso.with(getContext())
                .load(myMovie.image)
                .into(imageMovie);
        TextView releaseText = (TextView)view.findViewById(R.id.releasedate);
        releaseText.setText(myMovie.releaseDate);
        TextView overviewText = (TextView)view.findViewById(R.id.overview);
        overviewText.setText(myMovie.overView);

        TextView voteaverageText = (TextView)view.findViewById(R.id.voteaverage);
        voteaverageText.setText(myMovie.userRating);

        return view;
    }




}
