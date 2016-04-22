package com.example.android.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by rmenezes on 4/20/2016.
 */
@ContentProvider(authority = FavoritesProvider.AUTHORITY, database = FavoritesDatabase.class)
public class FavoritesProvider {

    public static final String AUTHORITY = "com.example.android.popularmovies.data.FavoritesProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String FAVORITES = "favorites";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = FavoritesDatabase.FAVORITES) public static class Favorites {

        @ContentUri(
                path = Path.FAVORITES,
                type = "vnd.android.cursor.dir/favorite",
                defaultSort = FavoritesColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITES);

        @InexactContentUri(
                path = Path.FAVORITES + "/#",
                name = "FAVORITE_ID",
                type = "vnd.android.cursor.item/favorite",
                whereColumn = FavoritesColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.FAVORITES, String.valueOf(id));
        }

        public static Uri withMovieID(String movieid){
            return buildUri(Path.FAVORITES, movieid);
        }
    }
}
