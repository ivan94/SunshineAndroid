package com.example.ivan.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ivan on 2/8/2015.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> adapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_refresh:
                updateWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] data = {
                "...",
        };

        List<String> dataList = new ArrayList<>(Arrays.asList(data));

        adapter = new ArrayAdapter<>(getActivity(),R.layout.list_item_forecast, R.id.list_item_forecast_textview, dataList);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String weather = adapter.getItem(position);

                /*Toast t = Toast.makeText(parent.getContext(), weather, Toast.LENGTH_SHORT);
                t.show();*/

                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra(Intent.EXTRA_TEXT, weather);
                startActivity(i);

            }
        });
        lv.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather(){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getActivity());
        new FetchWeatherTask(getActivity(), adapter).execute(p.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default)));
    }
//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{
//
//        //private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//        private final String BASE_PATH = "http://api.openweathermap.org/data/2.5/forecast/daily";
//        private final String QUERY_PARAM = "q";
//        private final String FORMAT_PARAM = "mode";
//        private final String UNITS_PARAM = "units";
//        private final String DAYS_PARAM = "cnt";
//
//        @Override
//        protected String[] doInBackground(String... params) {
//
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String forecastJsonStr;
//            String[] forecast = null;
//            String format = "json";
//            String units = "metric";
//            String days = "7";
//            String country = "usa";
//            try {
//
//                Uri.Builder builder = Uri.parse(BASE_PATH).buildUpon();
//                builder.appendQueryParameter(QUERY_PARAM, params[0] + ","+country);
//                builder.appendQueryParameter(FORMAT_PARAM, format);
//                builder.appendQueryParameter(UNITS_PARAM, units);
//                builder.appendQueryParameter(DAYS_PARAM, days);
//
//
//                // Construct the URL for the OpenWeatherMap query
//                // Possible parameters are available at OWM's forecast API page, at
//                // http://openweathermap.org/API#forecast
//                URL url = new URL(builder.toString());
//
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    forecastJsonStr = null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    forecastJsonStr = null;
//                }
//                forecastJsonStr = buffer.toString();
//
//                forecast = getWeatherDataFromJson(forecastJsonStr, Integer.parseInt(days));
//
//
//
//            } catch (IOException e) {
//                Log.e("ForecastFragment", "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attempting
//                // to parse it.
//                forecastJsonStr = null;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("ForecastFragment", "Error closing stream", e);
//                    }
//                }
//            }
//
//            return forecast;
//        }
//
//        @Override
//        protected void onPostExecute(String[] strings) {
//            if(strings == null)
//                return;
//            adapter.clear();
//            for(String s : strings){
//                adapter.add(s);
//            }
//            //((ListView)getView().findViewById(R.id.listview_forecast))
//        }
//
//        /* The date/time conversion code is going to be moved outside the asynctask later,
//                * so for convenience we're breaking it out into its own method now.
//                */
//        private String getReadableDateString(long time){
//            // Because the API returns a unix timestamp (measured in seconds),
//            // it must be converted to milliseconds in order to be converted to valid date.
//            Date date = new Date(time * 1000);
//            SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
//            return format.format(date).toString();
//        }
//
//        /**
//         * Prepare the weather high/lows for presentation.
//         */
//        private String formatHighLows(double high, double low) {
//            // For presentation, assume the user doesn't care about tenths of a degree.
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return highLowStr;
//        }
//
//        /**
//         * Take the String representing the complete forecast in JSON Format and
//         * pull out the data we need to construct the Strings needed for the wireframes.
//         *
//         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//         * into an Object hierarchy for us.
//         */
//        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DATETIME = "dt";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            String[] resultStrs = new String[numDays];
//            for(int i = 0; i < weatherArray.length(); i++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // The date/time is returned as a long.  We need to convert that
//                // into something human-readable, since most people won't read "1400356800" as
//                // "this saturday".
//                long dateTime = dayForecast.getLong(OWM_DATETIME);
//                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                double high = getPreferredTemperature(temperatureObject.getDouble(OWM_MAX));
//                double low = getPreferredTemperature(temperatureObject.getDouble(OWM_MIN));
//
//                highAndLow = formatHighLows(high, low);
//                resultStrs[i] = day + " - " + description + " - " + highAndLow;
//            }
//
//            return resultStrs;
//        }
//
//        private double getPreferredTemperature(double temp){
//            String prefTemp = PreferenceManager.getDefaultSharedPreferences(getActivity())
//                    .getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));
//
//            double result = temp;
//
//            if(prefTemp.equals(getResources().getStringArray(R.array.pref_units_entry_values)[1])){
//                result = (temp - 32)*5/9;
//            }
//
//            return result;
//        }
//    }
}
