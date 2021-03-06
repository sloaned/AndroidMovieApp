package com.example.catalyst.popmovies;

/**
 * Created by dsloane on 1/26/2016.
 */
public class UriBuilder {

    public UriBuilder() {}

    public String getUrl(String sortOrder) {
        System.out.println("sort order = " + sortOrder);
        String url = "https://api.themoviedb.org/3/";
        if (sortOrder.equals("popular")) {
            url += "discover/movie?sort_by=popularity.desc";
        }
        else if (sortOrder.equals("rating")) {
            url += "discover/movie?vote_count.gte=100&sort_by=vote_average.desc";
        }
        else if (sortOrder.equals("unpopular")) {
            url += "discover/movie?vote_count.gte=80&sort_by=popularity.asc";
        }
        else if (sortOrder.equals("lowrating")) {
            url += "discover/movie?vote_count.gte=100&sort_by=vote_average.asc";
        }
        /*
        if (page > 1) {
            url += "&page=" + page;
        } */

        url += "&api_key=" + BuildConfig.APIKEY;
        return url;
    }

    public String getSearchUrl(String searchTerm) {
        String url = "https://api.themoviedb.org/3/";
        url += "search/movie?query=";
        url += searchTerm;
        url += "&api_key=" + BuildConfig.APIKEY;
        return url;
    }

    public String getMovieUrl (String movieId) {
        String url = "https://api.themoviedb.org/3/movie/";
        url += movieId;
        url += "?api_key=" + BuildConfig.APIKEY;
        url += "&append_to_response=releases,trailers,reviews";
        return url;
    }

    public String getTrailerUrl (String trailerString) {
        String url = "https://www.youtube.com/watch?v="; // https
        url += trailerString;
        return url;
    }

    public String getCountryUrl (String countryCode, String cert) {
        String url = "https://api.themoviedb.org/3/discover/movie?";
        url += "certification_country=" + countryCode;
        url += "&certification.lte=" + cert;
        url += "&api_key=" + BuildConfig.APIKEY;
        return url;
    }
}
