package com.example.catalyst.popmovies;

/**
 * Created by dsloane on 1/26/2016.
 */
public class UriBuilder {

    public UriBuilder() {}

    public String getUrl(String sortOrder, int page) {
        System.out.println("sort order = " + sortOrder);
        String url = "https://api.themoviedb.org/3/";
        if (sortOrder.equals("popular")) {
            url += "discover/movie?sort_by=popularity.desc";
        }
        else if (sortOrder.equals("rating")) {
            url += "discover/movie?vote_count.gte=300&sort_by=vote_average.desc";
        }
        else if (sortOrder.equals("unpopular")) {
            url += "discover/movie?vote_count.gte=100&sort_by=popularity.asc";
        }
        else if (sortOrder.equals("lowrating")) {
            url += "discover/movie?vote_count.gte=300&sort_by=vote_average.asc";
        }
        if (page > 1) {
            url += "&page=" + page;
        }

        url += "&api_key=a40aba8d089b515623b55cc305b76b74";
        return url;
    }

    public String getSearchUrl(String searchTerm) {
        String url = "https://api.themoviedb.org/3/";
        url += "search/movie?query=";
        url += searchTerm;
        url += "&api_key=a40aba8d089b515623b55cc305b76b74";
        return url;
    }
}
