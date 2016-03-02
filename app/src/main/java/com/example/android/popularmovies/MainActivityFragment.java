package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivityFragment extends Fragment {

    private AndroidFlavorAdapter flavorAdapter;

    private ArrayList<AndroidFlavor> flavorList;

    AndroidFlavor[] androidFlavors = {
            new AndroidFlavor("Cupcake", "1.5",R.drawable.ic_launcher ),
            new AndroidFlavor("Donut", "1.6", R.drawable.ic_launcher),
            new AndroidFlavor("Eclair", "2.0-2.1", R.drawable.ic_launcher),
            new AndroidFlavor("Froyo", "2.2-2.2.3", R.drawable.ic_launcher),
            new AndroidFlavor("GingerBread", "2.3-2.3.7", R.drawable.ic_launcher),
            new AndroidFlavor("Honeycomb", "3.0-3.2.6", R.drawable.ic_launcher),
            new AndroidFlavor("Ice Cream Sandwich", "4.0-4.0.4", R.drawable.ic_launcher),
            new AndroidFlavor("Jelly Bean", "4.1-4.3.1", R.drawable.ic_launcher),
            new AndroidFlavor("KitKat", "4.4-4.4.4", R.drawable.ic_launcher),
            new AndroidFlavor("Lollipop", "5.0-5.1.1", R.drawable.ic_launcher)
    };

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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_main, container, false);

      flavorAdapter = new AndroidFlavorAdapter(getActivity(), Arrays.asList(androidFlavors));

      // Get a reference to the ListView, and attach this adapter to it.
      GridView gridView = (GridView) rootView.findViewById(R.id.flavors_grid);
      gridView.setAdapter(flavorAdapter);

      return rootView;
  }
}
