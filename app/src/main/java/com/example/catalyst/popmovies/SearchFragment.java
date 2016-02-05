package com.example.catalyst.popmovies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.catalyst.popmovies.data.DBHelper;
import com.example.catalyst.popmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class SearchFragment extends Fragment {
    private ArrayList<Movie> movies = new ArrayList<Movie>();
    private CustomListAdapter adapter;
    private ListView listView;
    private final String LOG_TAG = SearchFragment.class.getSimpleName();
    private Context context = getActivity();

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_search_movies);
        adapter = new CustomListAdapter(this.getActivity(), movies);
        //adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        System.out.println("in the search fragment");
        Intent intent = getActivity().getIntent();
        System.out.println(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            query = query.replace(" ", "%20");
            System.out.println("query = " + query);
            searchMovies(query);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) adapter.getItem(position);
                DBHelper dbHelper = new DBHelper(getContext());
                Cursor res = dbHelper.getMovieByInfo(movie);
                System.out.println(res);
                Movie film = new Movie();
                res.moveToFirst();
                film.setTitle(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                film.setRelease_date(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
                film.setOverview(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                film.setVote_average(res.getDouble(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                film.setPoster(res.getString(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
                film.setUser_rating(res.getDouble(res.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING)));
                film.setId(res.getInt(res.getColumnIndex(MovieContract.MovieEntry._ID)));
                System.out.println(film.getTitle());
               /*String movieInfo = movie.getTitle() + "\n\n" + movie.getRelease_date() + "\n\n" +
                       movie.getOverview() + "\n\n" + movie.getVote_average();
               System.out.println(movieInfo);*/


                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("Movie", film);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    protected void searchMovies(String query) {
        ArrayList<Movie> searchResults = new ArrayList<Movie>();
        UriBuilder uriBuilder = new UriBuilder();
        String url = uriBuilder.getSearchUrl(query);
        String tag_json_obj = "json_obj_req";
        final String TMDB_RESULTS = "results";
        System.out.println("now in searchMovies");
        System.out.println("url = " + url);
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
                            String poster = null;
                            String thumbnail = null;
                            JSONObject film = new JSONObject();
                            try {
                                film = movieArray.getJSONObject(i);
                                title = film.getString("title");
                                overview = film.getString("overview");
                                release_date = film.getString("release_date");
                                vote_average = film.getDouble("vote_average");

                                if(film.getString("poster_path") != null) {
                                    poster = "http://image.tmdb.org/t/p/" + "w185/" + film.getString("poster_path");
                                    thumbnail = "http://image.tmdb.org/t/p/" + "w45/" + film.getString("poster_path");
                                }


                                System.out.println(title);

                                Movie movie = new Movie();
                                movie.setTitle(title);
                                movie.setRelease_date(release_date);
                                movie.setVote_average(vote_average);
                                movie.setOverview(overview);
                                movie.setPoster(poster);
                                movie.setThumbnail(thumbnail);

                                movies.add(movie);
                                DBHelper dbHelper = new DBHelper(getActivity());  // not context
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

        AppController.getInstance().addToRequestQueue(req, tag_json_obj);

    }
}
