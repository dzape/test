package com.uacs.android.networking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.AsyncTask;

import android.widget.TextView;

import com.uacs.android.networking.data.SunshinePreferences;
import com.uacs.android.networking.utilities.NetworkUtils;
import com.uacs.android.networking.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;

import java.net.URL;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    // TODO (6) Add a TextView variable for the error message display
    TextView mErrorMessageDisplay;

    // TODO (16) Add a ProgressBar variable to show and hide the progress bar
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        // TODO (7) Find the TextView for the error message using findViewById
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message);

        // TODO (17) Find the ProgressBar using findViewById
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_bar);

        loadWeatherData();
    }

    private void loadWeatherData() {
        // TODO (20) Call showWeatherDataView before executing the AsyncTask
        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    // TODO (8) Create a method called showWeatherDataView that will hide the error message and show the weather data
    public void showWeatherDataView() {
        mWeatherTextView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }
    // TODO (9) Create a method called showErrorMessage that will hide the weather data and show the error message
    public void showErrorMessage() {
        mWeatherTextView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        // TODO (18) Within your AsyncTask, override the method onPreExecute and show the loading indicator
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            // TODO (19) As soon as the data is finished loading, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);


            // TODO (10) If the weather data was null, show the error message
            if (weatherData != null) {
                // TODO (11) If the weather data was not null, make sure the data view is visible
                /*
                 * Iterate through the array and append the Strings to the TextView. The reason why we add
                 * the "\n\n\n" after the String is to give visual separation between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                showWeatherDataView();
                for (String weatherString : weatherData) {
                    mWeatherTextView.append((weatherString) + "\n\n\n");
                }
            } else showErrorMessage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if (menuItemThatWasSelected == R.id.action_refresh) {

            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
