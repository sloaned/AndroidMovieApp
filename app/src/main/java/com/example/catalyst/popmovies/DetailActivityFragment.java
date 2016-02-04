package com.example.catalyst.popmovies;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.catalyst.popmovies.data.DBHelper;
import com.squareup.picasso.Picasso;

import uk.co.deanwild.flowtextview.FlowTextView;

public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private Movie movie;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra("Movie")) {
            movie = (Movie) intent.getSerializableExtra("Movie");
            System.out.println("here's the user rating: " + movie.getUser_rating());
            System.out.println("here's the id number: " + movie.getId());
            String movieInfo = movie.getTitle() + "\n\nReleased: " + movie.getRelease_date() + "\n\nAverage Rating: " + movie.getVote_average()
                    + "\n\n" + movie.getOverview();
            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
            if (movie.getPoster() != null) {
                Picasso.with(getContext()).load(movie.getPoster()).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.blankmovie);
            }

            FlowTextView flowTextView = (FlowTextView) rootView.findViewById(R.id.detail_text);
            flowTextView.setText(movieInfo);
           /* RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.user_rating_bar);
            Float floatRating = (float) movie.getUser_rating();
            ratingBar.setRating(floatRating);
            LayerDrawable drawable = (LayerDrawable) ratingBar.getProgressDrawable();

            DrawableCompat.setTint(DrawableCompat.wrap(drawable.getDrawable(1)), Color.GRAY);
            DrawableCompat.setTint(DrawableCompat.wrap(drawable.getDrawable(2)), Color.RED);
            DrawableCompat.setTint(DrawableCompat.wrap(drawable.getDrawable(0)), Color.GRAY);
            //ratingBar.getOnRatingBarChangeListener();
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    double doubleRating = (double) rating;
                    DBHelper dbHelper = new DBHelper(getContext());
                    dbHelper.updateMovie(movie.getId(), movie);
                    System.out.println("new rating = " + doubleRating);
                }
            }); */
        }
        return rootView;
    }

}