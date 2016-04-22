package com.example.android.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by rmenezes on 4/20/2016.
 */
public interface FavoritesColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT) @NotNull String TITLE = "title";
    @DataType(DataType.Type.TEXT) @NotNull String RELEASE_DATA = "release_date";
    @DataType(DataType.Type.TEXT) @NotNull String POSTER_PATH = "poster_path";
    @DataType(DataType.Type.TEXT) @NotNull String OVERVIEW = "overview";
    @DataType(DataType.Type.REAL) @NotNull String VOTE_AVERAGE = "vote_average";
}
