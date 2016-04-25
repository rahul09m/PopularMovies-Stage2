package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rahul on 24/04/2016.
 */
public class ReviewAdapter extends ArrayAdapter<Reviews> {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Activity context, List<Reviews> reviews) {
        super(context,0,reviews);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reviews reviews = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_review_movie, parent, false);
        }
        TextView author = (TextView)convertView.findViewById(R.id.review_author);
        TextView content =(TextView)convertView.findViewById(R.id.review_content);
        TextView urlView =(TextView)convertView.findViewById(R.id.review_url);

        author.setText(reviews.author);
        content.setText(reviews.content);
        urlView.setText(reviews.url);
        return convertView;
    }
}
