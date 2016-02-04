package com.example.catalyst.popmovies;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.catalyst.popmovies.data.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<Movie> movies;
    private CustomListAdapter adapter;
    private ListView listView;
    private final String LOG_TAG = SearchActivity.class.getSimpleName();
    private Context context = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // activity_search
        System.out.println("here I am in the search activity");
        movies = new ArrayList<Movie>();

        listView = (ListView) findViewById(R.id.listview_movies);
        adapter = new CustomListAdapter(this, movies);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        System.out.println(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchMovies(query);
        }
    }




    protected void searchMovies(String query) {
        ArrayList<Movie> searchResults = new ArrayList<Movie>();
        UriBuilder uriBuilder = new UriBuilder();
        String url = uriBuilder.getSearchUrl(query);
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
                        for(int i = 0; i < movieArray.length(); i++) {
                            String title;
                            String overview;
                            String release_date;
                            double vote_average;
                            String poster;
                            String thumbnail;
                            JSONObject film = new JSONObject();
                            try {
                                film = movieArray.getJSONObject(i);
                                title = film.getString("title");
                                overview = film.getString("overview");
                                release_date = film.getString("release_date");
                                vote_average = film.getDouble("vote_average");
                                poster = "http://image.tmdb.org/t/p/" + "w185/" + film.getString("poster_path");
                                thumbnail = "http://image.tmdb.org/t/p/" + "w45/" + film.getString("poster_path");

                                Movie movie = new Movie();
                                movie.setTitle(title);
                                movie.setRelease_date(release_date);
                                movie.setVote_average(vote_average);
                                movie.setOverview(overview);
                                movie.setPoster(poster);
                                movie.setThumbnail(thumbnail);

                                movies.add(movie);
                                DBHelper dbHelper = new DBHelper(context);  // probably wrong
                                if(!dbHelper.doesMovieExist(movie)) {
                                    dbHelper.addMovie(movie);
                                }
                            }catch (JSONException e) {
                                Log.e(LOG_TAG, "Error: " + e.getMessage());
                            }

                            adapter.notifyDataSetChanged();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });



    }

}
