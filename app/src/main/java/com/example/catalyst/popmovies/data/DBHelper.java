package com.example.catalyst.popmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.catalyst.popmovies.Movie;

import java.util.ArrayList;

/**
 * Created by dsloane on 2/1/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.MovieEntry.TABLE_NAME + " (" +
            MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieContract.MovieEntry.COLUMN_TITLE + " REAL NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " REAL NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_POSTER_PATH + " REAL, " +
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_FAVORITE + " NUMERIC NOT NULL)";

    public DBHelper(Context context) {
        super(context, MovieContract.DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    public boolean addMovie(Movie movie) {
        System.out.println("in addMovie");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());

        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster());

        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, movie.getFavorite());
        db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public Cursor getMovieById(int id) {
        System.out.println("in getMovieById");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE id=" +id+"", null );
        //db.close();
        return res;
    }

    public Cursor getMovieByInfo(Movie movie) {
        System.out.println("in getMovieByInfo");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry.COLUMN_TITLE + " = \"" + movie.getTitle() + "\" AND " + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " = \"" + movie.getRelease_date() + "\"", null);
        //db.close();
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MovieContract.MovieEntry.TABLE_NAME);
        db.close();
        return numRows;
    }

    public boolean updateMovie (Integer id, Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster());
        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, movie.getFavorite());
        db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, MovieContract.MovieEntry._ID + " = ? ", new String[] { Integer.toString(id) } );
        db.close();
        return true;
    }

    public Integer deleteMovie (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MovieContract.MovieEntry.TABLE_NAME,
                MovieContract.MovieEntry._ID + " = ? ",
                new String[] { Integer.toString(id) } );
    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + "", null);
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            Movie movie = new Movie();
            movie.setTitle(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movie.setOverview(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            movie.setVote_average(res.getDouble(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            movie.setPoster(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            movie.setRelease_date(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movie.setFavorite(res.getInt(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)));
            movieList.add(movie);
            res.moveToNext();
        }
        res.close();
        db.close();
        return movieList;
    }

    public boolean doesMovieExist(Movie movie) {
        System.out.println("in doesMovieExist");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry.COLUMN_TITLE + " = \"" + movie.getTitle() + "\" AND " + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " = \"" + movie.getRelease_date() + "\"", null);
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            Movie film = new Movie();
            film.setTitle(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            film.setOverview(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            film.setVote_average(res.getDouble(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            film.setPoster(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            film.setRelease_date(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            film.setFavorite(res.getInt(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)));
            movieList.add(film);
            res.moveToNext();
        }
        res.close();
        db.close();
        if(movieList.size() > 0) {
            //System.out.println("here's the movie found: " + movieList.get(0).getTitle());
            return true;
        }
        return false;
    }

}
