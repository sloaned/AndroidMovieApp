package com.example.catalyst.popmovies;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.catalyst.popmovies.data.DBHelper;
import com.example.catalyst.popmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.Serializable;
import java.util.prefs.PreferenceChangeEvent;

public class MovieFragment extends Fragment {

    private CustomListAdapter adapter;
    private ListView listView;
    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private final int MOVIES_PER_PAGE = 20;
    private final int NUM_MOVIES = 100;
    private int numPages;
    private List<Movie> movieList = new ArrayList<Movie>();

    public MovieFragment() {}

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_movies);
        adapter = new CustomListAdapter(this.getActivity(), movieList);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Movie movie = (Movie) adapter.getItem(position);

               Intent intent = new Intent(getActivity(), DetailActivity.class)
                       .putExtra("Movie", movie);
               startActivity(intent);
           }
       });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getActivity().getIntent();
        String url;
        UriBuilder uriBuilder = new UriBuilder();
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            System.out.println("query made for: " + query);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putString(getString(R.string.pref_search_key), query);
            editor.putString(getString(R.string.pref_saved_search), query);
            editor.apply();
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String storedSearch = preferences.getString(getString(R.string.pref_search_key), null);
        System.out.println("storedSearch = " + storedSearch);

        /* used to initiate search upon app resume */
        if (storedSearch != null) {
            intent.setAction(Intent.ACTION_SEARCH).putExtra(SearchManager.QUERY, storedSearch);
        }
        movieList.clear();
        adapter.notifyDataSetChanged();

        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) { // perform a search
            String query = intent.getStringExtra(SearchManager.QUERY);
            query = query.replace(" ", "%20");
            url = uriBuilder.getSearchUrl(query);
            updateMovies(url);
        } else {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortOrder = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
            String country = prefs.getString(getString(R.string.country_filter), (String) null);
            if (country != null && !country.equals((String) null)) { // filter movies by country
                String rating = prefs.getString(getString(R.string.certification_filter), (String) null);
                url = uriBuilder.getCountryUrl(country, rating);
                updateMovies(url);
            }
            else if (!sortOrder.equals("favorites")) { 
                url = uriBuilder.getUrl(sortOrder);
                updateMovies(url);
            } else {
                showFavorites();
            }
        }

    }

    private void updateMovies(String url) {
        adapter.clear();
        getMovieData(url);
        /*for (int i = 2; i <= 5 && i <= numPages; i++) {
            getMovieData(i);
        }*/
    }

    protected void showFavorites() {
        adapter.clear();
        DBHelper dbHelper = new DBHelper(getActivity());
        Cursor res = dbHelper.getFavoriteMovies();
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {
                Movie movie = new Movie();
                movie.setTitle(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                movie.setPoster(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
                movie.setThumbnail(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_THUMBNAIL)));
                movie.setOverview(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                movie.setVote_average(res.getDouble(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                movie.setId(res.getInt(res.getColumnIndex(MovieContract.MovieEntry._ID)));
                movie.setRelease_date(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
                movie.setFavorite(1);
                movie.setFavorite_date(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE_DATE)));
                movieList.add(movie);
                res.moveToNext();
            }
            adapter.notifyDataSetChanged();
            res.close();
        }
    }

    public void getSingleMovieData(Movie movie) {
        UriBuilder uriBuilder = new UriBuilder();
        String url = uriBuilder.getMovieUrl(movie.getTmdb_id());
        String tag_json_obj = "json_obj_req";
        final String MOVIE_TRAILERS = "trailers";
        final String YOUTUBE_TRAILERS = "youtube";
        final String YOUTUBE_SOURCE = "source";
        final String MOVIE_REVIEWS = "reviews";
        final String REVIEW_RESULTS = "results";
        final Movie MOVIE_FILM = movie;
        boolean hasTrailer;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                        Movie film = MOVIE_FILM;
                        try{
                            JSONObject trailers = response.getJSONObject(MOVIE_TRAILERS);
                            JSONArray youtubeTrailers = trailers.getJSONArray(YOUTUBE_TRAILERS);

                            if (youtubeTrailers.length() > 0) {
                                film.setHasTrailer(true);
                                JSONObject firstTrailer = youtubeTrailers.getJSONObject(0);
                                film.setTrailer(firstTrailer.getString(YOUTUBE_SOURCE));
                            }

                            JSONObject reviews = response.getJSONObject(MOVIE_REVIEWS);
                            JSONArray reviewResults = reviews.getJSONArray(REVIEW_RESULTS);

                            if (reviewResults.length() > 0) {
                                film.setHasReviews(true);
                            }

                            DBHelper dbHelper = new DBHelper(getContext());
                            dbHelper.addMovie(film);
                            Cursor res = dbHelper.getMovieByTMDBId(film);
                            res.moveToFirst();

                            film.setFavorite(res.getInt(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)));
                            film.setId(res.getInt(res.getColumnIndex(MovieContract.MovieEntry._ID)));
                            res.close();
                            System.out.println("adding to database: " + film.getTitle() + ", id = " + film.getId());

                            dbHelper.close();
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Error: " + e.getMessage());
                        }

                        movieList.add(film);

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }


    public void getMovieData(String url) {

        //final int page = queryPage;
        String tag_json_obj = "json_obj_req";
        final String TMDB_RESULTS = "results";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());

                        JSONArray movieArray = new JSONArray();

                        try{
                            movieArray = response.getJSONArray(TMDB_RESULTS);
                        } catch(JSONException e) {
                            Log.e(LOG_TAG, "Error: " + e.getMessage());
                        }
                        for(int i = 0; i < MOVIES_PER_PAGE && i < movieArray.length(); i++) {
                            String title;
                            String overview;
                            String poster = null;
                            String thumbnail = null;
                            JSONObject film = new JSONObject();
                            try{
                                film = movieArray.getJSONObject(i);
                                title = film.getString("title");
                                title = title.replaceAll("\"", "\"\"");
                                overview = film.getString("overview");
                                overview = overview.replaceAll("\"", "\"\"");
                                if(!film.getString("poster_path").equals("null")) {
                                    poster = "http://image.tmdb.org/t/p/" + "w185/" + film.getString("poster_path");
                                    thumbnail = "http://image.tmdb.org/t/p/" + "w45/" + film.getString("poster_path");
                                }

                                Movie movie = new Movie();
                                movie.setTitle(title);
                                movie.setRelease_date(film.getString("release_date"));
                                movie.setVote_average(film.getDouble("vote_average"));
                                movie.setOverview(overview);
                                movie.setPoster(poster);
                                movie.setThumbnail(thumbnail);
                                movie.setTmdb_id(film.getString("id"));

                                DBHelper dbHelper = new DBHelper(getContext());
                                if(!dbHelper.doesMovieExist(movie)) {
                                    dbHelper.close();
                                    getSingleMovieData(movie);
                                } else {
                                    System.out.println("already in db: " + movie.getTitle());

                                    Cursor res = dbHelper.getMovieByTMDBId(movie);
                                    res.moveToFirst();

                                    movie.setFavorite(res.getInt(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)));
                                    movie.setId(res.getInt(res.getColumnIndex(MovieContract.MovieEntry._ID)));
                                    if (res.getInt(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_HAS_TRAILER)) == 1) {
                                        movie.setHasTrailer(true);
                                        movie.setTrailer(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILER)));
                                    }
                                    if (res.getInt(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_HAS_REVIEWS)) == 1) {
                                        movie.setHasReviews(true);
                                    }
                                    res.close();
                                    dbHelper.close();

                                    movieList.add(movie);
                                }

                            } catch (JSONException e) {
                                Log.e(LOG_TAG, "Error: " + e.getMessage());
                            }

                        }
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }
}
