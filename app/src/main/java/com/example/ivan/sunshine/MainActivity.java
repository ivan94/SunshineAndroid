package com.example.ivan.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private String mLocation;
    private static String FORECASTFRAGMENT_TAG = "com.example.ivan.sunshine.ForecastFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
        mLocation = Utility.getPreferredLocation(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String storedLocation = Utility.getPreferredLocation(this);
        if(!storedLocation.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            ff.onLocationChanged();
            mLocation = storedLocation;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String BASE_GEO_PATH = "geo:0,0";
        final String QUERY_PARAM = "q";
        final String FAIL_INTENT_MSG = "No map application found";

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if(id == R.id.action_show_on_map){
            String loc = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            Uri.Builder bd = Uri.parse(BASE_GEO_PATH).buildUpon();
            bd.appendQueryParameter(QUERY_PARAM, loc);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(bd.build());

            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }else{
                Toast.makeText(this, FAIL_INTENT_MSG, Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
}
