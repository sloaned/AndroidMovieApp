package com.example.catalyst.popmovies;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.catalyst.popmovies.data.DBHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.deanwild.flowtextview.FlowTextView;

public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private Movie movie;
    private String trailer;

    public DetailActivityFragment() {}


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setHasOptionsMenu(true);
    }

    public void onToggleStar(ImageButton btn) {

        if (movie.getFavorite() == 0) {
            movie.setFavorite(1);
            Date date = new Date();
            //String dateString = date.toString();
            String dateString;
            SimpleDateFormat dt = new SimpleDateFormat("MM/dd/yyyy");

            dateString = dt.format(date);


            movie.setFavorite_date(dateString);
            System.out.println(movie.getFavorite_date());
            btn.setImageResource(R.drawable.button_pressed);
        } else {
            movie.setFavorite(0);
            btn.setImageResource(R.drawable.button_normal);
        }

        DBHelper dbHelper = new DBHelper(this.getActivity());
        dbHelper.updateMovie(movie.getId(), movie);
        dbHelper.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra("Movie")) {
            movie = (Movie) intent.getSerializableExtra("Movie");
            System.out.println("here's the id number: " + movie.getId());
            String movieInfo = movie.getTitle().replaceAll("\"\"", "\"") + "\n\nReleased: " + movie.getRelease_date() + "\n\nAverage Rating: " + movie.getVote_average()
                    + "\n\n" + movie.getOverview().replaceAll("\"\"", "\"");
            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
            if (movie.getPoster() != null) {
                Picasso.with(getContext()).load(movie.getPoster()).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.blankmovie);
            }
            System.out.println(movie.getTitle() + " trailer?: " + movie.getHasTrailer());
            if (movie.getHasTrailer()) {
                showTrailerLink(rootView);
            }

            if (movie.getHasReviews()) {
                showReviewLink(rootView);
            }

            FlowTextView flowTextView = (FlowTextView) rootView.findViewById(R.id.detail_text);
            flowTextView.setText(movieInfo);

            final ImageButton btn = (ImageButton) rootView.findViewById(R.id.favorite);
            if (movie.getFavorite() == 1) {
                btn.setImageResource(R.drawable.button_pressed);
            } else {
                btn.setImageResource(R.drawable.button_normal);
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onToggleStar(btn);
                }
            });
        }
        return rootView;
    }

    public void showTrailerLink(View rootView) {
        TextView trailerLink = (TextView) rootView.findViewById(R.id.trailer_link);
        trailerLink.setText("View trailer");
        trailerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTrailer();
            }
        });
    }

    public void showReviewLink(View rootView) {
        TextView reviewLink = (TextView) rootView.findViewById(R.id.reviews_link);
        reviewLink.setText("Read reviews");
    }

    public void onClickTrailer() {
        UriBuilder uriBuilder = new UriBuilder();

        String youtubeUrl = uriBuilder.getTrailerUrl(movie.getTrailer());
        System.out.println("trailer suffix = " + movie.getTrailer());
        System.out.println("youtube url = " + youtubeUrl);

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl)));
        /*Intent intent = new Intent(Intent.ACTION_VIEW);

        if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
            System.out.println("no activity available");
        } else {
            startActivity(intent);
        }*/
    }

    public void getTrailerLink(String url) {
        String tag_json_obj = "json_obj_req";
        final String MOVIE_TRAILERS = "trailers";
        final String YOUTUBE_TRAILERS = "youtube";
        final String YOUTUBE_SOURCE = "source";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONObject trailers = response.getJSONObject(MOVIE_TRAILERS);
                            JSONArray  youtubeTrailers = trailers.getJSONArray(YOUTUBE_TRAILERS);
                            JSONObject firstTrailer = youtubeTrailers.getJSONObject(0);
                            String trailerSource = firstTrailer.getString(YOUTUBE_SOURCE);
                            trailer = trailerSource;
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Error: " + e.getMessage());
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