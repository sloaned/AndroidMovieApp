package com.example.catalyst.popmovies;

import java.io.Serializable;

/**
 * Created by dsloane on 1/26/2016.
 */
public class Movie implements Serializable{

    public Movie(){ favorite = 0; }

    private int id;
    private String title;
    private String poster;
    private String thumbnail;
    private String overview;
    private double vote_average;
    private String release_date;
    private int favorite;

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public String getPoster() {
        return poster;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }
    public String getOverview() {
        return overview;
    }
    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }
    public double getVote_average() {
        return vote_average;
    }
    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
    public String getRelease_date() {
        return release_date;
    }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public String getThumbnail() { return thumbnail; }
    public void setFavorite(int favorite) { this.favorite = favorite; }
    public double getFavorite() { return favorite; }
}
