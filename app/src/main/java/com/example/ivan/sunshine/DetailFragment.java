package com.example.ivan.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ivan.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private int WEATHER_LOADER = 1;

    ShareActionProvider mShareActionProvider;
    String mWeather;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_CONDITION_ID = 5;
    private static final int COL_WEATHER_HUMIDITY= 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_PRESSURE = 9;
    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailsfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if(mWeather != null)
            mShareActionProvider.setShareIntent(getShareIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private Intent getShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mWeather+"#SunshineApp");
        return intent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(WEATHER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if(intent == null){
            return null;
        }

        return new CursorLoader(getActivity(),intent.getData(), FORECAST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()) return;

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);

        int weatherIconRes = Utility.getArtResourceForWeatherCondition(weatherId);

        long dateInMillis = data.getLong(COL_WEATHER_DATE);
        String dayName = Utility.getDayName(getActivity(),dateInMillis);
        String dateString = Utility.getFormattedMonthDay(getActivity(), dateInMillis);

        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        String description = data.getString(COL_WEATHER_DESC);

        String humidity = getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUMIDITY));
        String wind = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES));
        String pressure = getString(R.string.format_pressure, data.getFloat(COL_WEATHER_PRESSURE));

        TextView dayNameView = (TextView) getView().findViewById(R.id.details_dayname_textview);
        dayNameView.setText(dayName);

        TextView dateView = (TextView) getView().findViewById(R.id.details_date_textview);
        dateView.setText(dateString);

        TextView highView = (TextView) getView().findViewById(R.id.details_high_textview);
        highView.setText(high);

        TextView lowView = (TextView) getView().findViewById(R.id.details_low_textview);
        lowView.setText(low);

        ImageView iconView = (ImageView) getView().findViewById(R.id.details_icon);
        iconView.setImageResource(weatherIconRes);

        TextView descView = (TextView) getView().findViewById(R.id.details_forecast_textview);
        descView.setText(description);

        TextView humidityView = (TextView) getView().findViewById(R.id.details_humidity_textview);
        humidityView.setText(humidity);

        TextView windView = (TextView) getView().findViewById(R.id.details_wind_textview);
        windView.setText(wind);

        TextView pressureView = (TextView) getView().findViewById(R.id.details_pressure_textview);
        pressureView.setText(pressure);


        mWeather = String.format("%s - %s - %s/%s", dateString, description, high, low);


        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(getShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
