package com.example.pratik.popularmovieapp1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailMovieActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailMovieActivityFragment.class.getSimpleName();
    private String movieDetailString;

    public DetailMovieActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // return inflater.inflate(R.layout.fragment_detail_movie, container, false);
        View rootView = inflater.inflate(R.layout.fragment_detail_movie, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieDetailString = intent.getStringExtra(Intent.EXTRA_TEXT);

            String[] singleMovieStr = movieDetailString.split("\\s*;;\\s*");
            /*
            singleMovieStr[0] => movieTitle
            singleMovieStr[1] => moviePosterLink
            singleMovieStr[2] => movieOverview
            singleMovieStr[3] => movieVote
            singleMovieStr[4] => movieReleaseDate

             */

            /*
             typecasting for the singleMovieStr[3] which is rating
             string to float
             */
            float ratingStars = 0;

            try {
                 ratingStars = Float.parseFloat(singleMovieStr[3]);
            } catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            //divided by 2 as we have rating bar of 5 stars
            ratingStars = ratingStars/2;

            /*
              Date Format
              dateStr[0] => year
              dateStr[1] => month
              dateStr[2] => day
             */

            String[] dateStr = singleMovieStr[4].split("\\s*-\\s*");
            int monthInt = 0;

            try {
                monthInt = Integer.parseInt(dateStr[1]);
            } catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            String months[] = {"January", "February", "March", "April",
                    "May", "June", "July", "August", "September",
                    "October", "November", "December"};

            String dateString = months[monthInt-1];
            dateString = dateStr[2]+' '+dateString+", "+dateStr[0];

                    ((TextView) rootView.findViewById(R.id.movieHeading))
                    .setText(singleMovieStr[0]);

            ImageView iconView = (ImageView) rootView.findViewById(R.id.movie_image);
            Picasso.with(getContext())
                    .load(singleMovieStr[1])
                    .placeholder(R.drawable.placeholder)
                    .fit().centerCrop()
                    .into(iconView);

            RatingBar ratingStar = (RatingBar) rootView.findViewById(R.id.ratingBar);
            ratingStar.setRating(ratingStars);

            ((TextView) rootView.findViewById(R.id.ratingText))
                    .setText(singleMovieStr[3] + "/10");

            ((TextView) rootView.findViewById(R.id.releaseDate))
                    .setText(dateString);

            ((TextView) rootView.findViewById(R.id.movieOverview))
                    .setText(singleMovieStr[2]);
        }

        return rootView;
    }
}
