package com.example.catalyst.popmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
       //adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.requestLayout();


       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Movie movie = (Movie) adapter.getItem(position);

               Intent intent = new Intent(getActivity(), DetailActivity.class)
                       .putExtra("Movie", movie);
               /*Intent intent = new Intent(getActivity(), TrailerActivity.class); */
               startActivity(intent);
           }
       });
        return rootView;
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }*/

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        adapter.clear();
        getMovieData(1);
        /*for (int i = 2; i <= 5 && i <= numPages; i++) {
            getMovieData(i);
        }*/
    }

    public void getMovieData(int queryPage) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        UriBuilder uriBuilder = new UriBuilder();
        String url = uriBuilder.getUrl(sortOrder, queryPage);
        final int page = queryPage;
        String tag_json_obj = "json_obj_req";
        final String TMDB_RESULTS = "results";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());

                        JSONArray movieArray = new JSONArray();

                        try{
                            if(page == 1){
                                numPages = response.getInt("total_pages");
                                System.out.println("numPages = " + numPages);
                            }

                            movieArray = response.getJSONArray(TMDB_RESULTS);
                        } catch(JSONException e) {
                            Log.e(LOG_TAG, "Error: " + e.getMessage());
                        }
                        for(int i = 0; i < MOVIES_PER_PAGE && i < movieArray.length(); i++) {
                            String title;
                            String overview;
                            String release_date;
                            double vote_average;
                            String poster = null;
                            String thumbnail = null;
                            JSONObject film = new JSONObject();
                            try{
                                film = movieArray.getJSONObject(i);
                                title = film.getString("title");
                                overview = film.getString("overview");
                                release_date = film.getString("release_date");
                                vote_average = film.getDouble("vote_average");
                                if(!film.getString("poster_path").equals("null")) {
                                    poster = "http://image.tmdb.org/t/p/" + "w185/" + film.getString("poster_path");
                                    thumbnail = "http://image.tmdb.org/t/p/" + "w45/" + film.getString("poster_path");
                                } else { System.out.println("null poster!"); }

                                Movie movie = new Movie();
                                movie.setTitle(title);
                                movie.setRelease_date(release_date);
                                movie.setVote_average(vote_average);
                                movie.setOverview(overview);
                                movie.setPoster(poster);
                                movie.setThumbnail(thumbnail);

                                DBHelper dbHelper = new DBHelper(getActivity());  // not context
                                if(!dbHelper.doesMovieExist(movie)) {
                                    dbHelper.addMovie(movie);
                                }

                                Cursor res = dbHelper.getMovieByInfo(movie);
                                res.moveToFirst();

                                movie.setFavorite(res.getInt(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)));
                                movie.setId(res.getInt(res.getColumnIndex(MovieContract.MovieEntry._ID)));

                                movieList.add(movie);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
