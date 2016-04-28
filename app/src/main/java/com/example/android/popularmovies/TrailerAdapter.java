package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rmenezes on 4/25/2016.
 */
public class TrailerAdapter extends ArrayAdapter<Trailers> {
    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Activity context, List<Trailers> trailers) {
        super(context,0,trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailers trailers = getItem(position);
        final String trailerUrl = "https://www.youtube.com/watch?v=" + trailers.source;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_trailer_movie, parent, false);
        }

        ImageView imgView = (ImageView)convertView.findViewById(R.id.trailer_img);
        imgView.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        TextView name = (TextView)convertView.findViewById(R.id.trailer_name);
        name.setText(trailers.name);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                getContext().startActivity(shareIntent);
            }
        });
        return convertView;
    }
}

