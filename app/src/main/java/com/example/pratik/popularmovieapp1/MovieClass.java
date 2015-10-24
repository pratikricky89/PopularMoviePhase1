package com.example.pratik.popularmovieapp1;

/**
 * Created by Pratik on 20-10-2015.
 */
public class MovieClass {
    String movieTitle;
    String moviePosterLink;
    String movieOverview;
    String movieVote;
    String movieReleaseDate;

    public MovieClass(String vMovieTitle, String vMoviePosterLink, String vMovieOverview, String vMovieVote, String vMovieReleaseDate )
    {
        this.movieTitle = vMovieTitle;
        this.moviePosterLink = vMoviePosterLink;
        this.movieOverview = vMovieOverview;
        this.movieVote = vMovieVote;
        this.movieReleaseDate = vMovieReleaseDate;
    }
}
