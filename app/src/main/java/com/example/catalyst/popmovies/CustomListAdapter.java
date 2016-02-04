package com.example.catalyst.popmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.NetworkInfo;
import android.net.sip.SipSession;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsloane on 1/27/2016.
 */
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Movie> movieItems = new ArrayList<Movie>();
    ImageLoader imageLoader = AppController.getInstance().getmImageLoader();

    public CustomListAdapter(Activity activity, List<Movie> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
    }

    public void clear() {
        movieItems.clear();
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String viewId = parent.getResources().getResourceEntryName(parent.getId());
        if (inflater == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            if(viewId.equals("listview_movies")) {
                convertView = inflater.inflate(R.layout.list_pop_movies, null);
            }
            else if (viewId.equals("listview_search_movies")) {
                convertView = inflater.inflate(R.layout.list_search_results, null);
            }
        }
        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getmImageLoader();
        }

        TextView title = (TextView) convertView.findViewById(R.id.movie_title);
        TextView rating = (TextView) convertView.findViewById(R.id.movie_rating);
        TextView year = (TextView) convertView.findViewById(R.id.movie_year);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.movie_rating_bar);
        Movie m = movieItems.get(position);

        if(m.getThumbnail() != null) {
            NetworkImageView thumbnail = (NetworkImageView) convertView
                    .findViewById(R.id.poster_icon);
            thumbnail.setImageUrl(m.getThumbnail(), imageLoader);
        }


        title.setText(m.getTitle());
        rating.setText("Rating: " + Double.toString(m.getVote_average()));
        String yearString = "";
        if((m.getRelease_date()).length() > 4) {
            yearString = m.getRelease_date().substring(0, 4);
        }
        year.setText(yearString);
        Float floatRating = (float) m.getVote_average();

        LayerDrawable drawable = (LayerDrawable) ratingBar.getProgressDrawable();

        DrawableCompat.setTint(DrawableCompat.wrap(drawable.getDrawable(1)), Color.GRAY);
        DrawableCompat.setTint(DrawableCompat.wrap(drawable.getDrawable(2)), Color.RED);
        DrawableCompat.setTint(DrawableCompat.wrap(drawable.getDrawable(0)), Color.GRAY);

        ratingBar.setRating(floatRating);

        return convertView;
    }

}
