package com.example.catalyst.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dsloane on 2/1/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.catalyst.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final String DATABASE_NAME = "Movies.db";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_THUMBNAIL = "thumbnail";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_FAVORITE = "favorite";

        public static final String COLUMN_FAVORITE_DATE = "favorite_date";

        public static final String COLUMN_HAS_TRAILER = "has_trailer";

        public static final String COLUMN_HAS_REVIEWS = "has_reviews";

        public static final String COLUMN_TMDB_ID = "tmdb_id";

        public static final String COLUMN_TRAILER = "trailer";

    }


}
