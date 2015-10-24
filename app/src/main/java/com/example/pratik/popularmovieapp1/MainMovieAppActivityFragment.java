package com.example.pratik.popularmovieapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMovieAppActivityFragment extends Fragment {

    private MainMovieAdapter mMovieList;

    public MainMovieAppActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //add this line in order this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.movie_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_popular));
        movieTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main_movie_app, container, false);

        View rootView = inflater.inflate(R.layout.fragment_main_movie_app, container, false);

       // mMovieList = new MainMovieAdapter(getActivity(), Arrays.asList(movieList));

        ArrayList<MovieClass> lst = new ArrayList<>();

        mMovieList = new MainMovieAdapter(getActivity(), lst);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(mMovieList);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                /* Explicit intent to launch DetailMovieActivity*/
                MovieClass movieDetails = mMovieList.getItem(position);
                //Create String to pass
                String movieDetailString = movieDetails.movieTitle + " ;; "
                                           + movieDetails.moviePosterLink + " ;; "
                                           + movieDetails.movieOverview + " ;; "
                                           + movieDetails.movieVote + " ;; "
                                           + movieDetails.movieReleaseDate ;
                Intent intent = new Intent(getActivity(), DetailMovieActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movieDetailString);
                startActivity(intent);


            }
        });

        return rootView;
    }



    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        //defining LOG TAG
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException
        {
            /*
             tags to be extracted as per the requirement
             original_title i.e original title
             poster_path i.e movie poster image thumbnail
             overview i.e A plot synopsis (called overview in the api)
             vote_average i.e user rating (called vote_average in the api)
             release_date i.e release date
            */
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWM_TITLE = "original_title";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_VOTE = "vote_average";
            final String OWM_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            String[] resultStrs = new String[movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {

                String title;
                String poster_path;
                String overview;
                String vote;
                String release_date;

                // Get the JSON object representing the movie
                JSONObject movieArrayOnebyOne = movieArray.getJSONObject(i);

                //extracting the details
                title = movieArrayOnebyOne.getString(OWM_TITLE);
                poster_path = "http://image.tmdb.org/t/p/w342" + movieArrayOnebyOne.getString(OWM_POSTER_PATH);
                overview = movieArrayOnebyOne.getString(OWM_OVERVIEW);
                vote = movieArrayOnebyOne.getString(OWM_VOTE);
                release_date = movieArrayOnebyOne.getString(OWM_RELEASE_DATE);

                resultStrs[i] = title + " ;; " + poster_path + " ;; " + overview + " ;; " + vote + " ;; " + release_date ;


            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }
            return resultStrs;

        }


        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the theMovieDb query
                // Possible parameters are available at OWM's forecast API page, at
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=XXXXXXXXXXX
                final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?api_key=XXXXXXXXXXX";
                final String SORT_PARAM = "sort_by";

                Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                //Added to check the json returned
                Log.v(LOG_TAG,"Movie json string" + movieJsonStr );

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error Here :: ", e);

                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG,"Error closing stream", e);
                    }
                }
            }
              //  return movieJsonStr;

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }



            // This will only happen if there was an error getting or parsing the movieJsonStr.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mMovieList.clear();
                MovieClass[] movieList = new MovieClass[result.length];

                for(int i = 0 ; i < result.length ; i++ ) {

                    String[] singleMovieStr = result[i].split("\\s*;;\\s*");
                    movieList[i]= new MovieClass(singleMovieStr[0],singleMovieStr[1],singleMovieStr[2],singleMovieStr[3],singleMovieStr[4]);
                    mMovieList.add(movieList[i]);


                }
            }
        }


    }
}
