package com.example.ivan.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
    public static final String ARGUMENT_URI = "data_uri";

    ShareActionProvider mShareActionProvider;
    String mForecast;
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
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
    private ImageView mIconView;
    private TextView mDateView;
    private TextView mFriendlyDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.ARGUMENT_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.details_icon);
        mDateView = (TextView) rootView.findViewById(R.id.details_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.details_dayname_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.details_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.details_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.details_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.details_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.details_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.details_pressure_textview);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailsfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if(mForecast != null)
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
        intent.putExtra(Intent.EXTRA_TEXT, mForecast +"#SunshineApp");
        return intent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(WEATHER_LOADER, getArguments(), this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null == mUri ) {
            return null;
        }

        return new CursorLoader(getActivity(), mUri , DETAIL_COLUMNS, null, null, null);
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

        mFriendlyDateView.setText(dayName);

        mDateView.setText(dateString);

        mHighTempView.setText(high);

        mLowTempView.setText(low);

        mIconView.setImageResource(weatherIconRes);


        mDescriptionView.setText(description);

        mHumidityView.setText(humidity);

        mWindView.setText(wind);

        mPressureView.setText(pressure);


        mForecast = String.format("%s - %s - %s/%s", dateString, description, high, low);


        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(getShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
