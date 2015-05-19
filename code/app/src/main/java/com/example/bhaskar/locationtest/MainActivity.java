package com.example.bhaskar.locationtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "basic-location-sample";
    private float totalwalk;

    protected GoogleApiClient mGoogleApiClient;
    GoogleMap googleMap;
    Marker TP;
    protected Location mLastLocation;

    private SharedPreferences preference;
    private final String PrefTAG = "LocationTest";
    private final String ArrayTAG = "LocationArray";
    private final String DataTAG = "Walked";
    Polyline line;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preference = getSharedPreferences(PrefTAG, MODE_PRIVATE);


        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setBuildingsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        startService(new Intent(getBaseContext(), MainService.class));

//--------------------------------------------------------------------------------------------------


// The minimum time (in miliseconds) the system will wait until checking if the location changed
        int minTime = 5000;
// The minimum distance (in meters) traveled until you will be notified
        float minDistance = 7;
// Create a new instance of the location listener
        MyLocationListener myLocListener = new MyLocationListener();
// Get the location manager from the system
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
// Get the criteria you would like to use
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(false);
// Get the best provider from the criteria specified, and false to say it can turn the provider on if it isn't already
        String bestProvider = locationManager.getBestProvider(criteria, false);
// Request location updates
        locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener);


//--------------------------------------------------------------------------------------------------


        buildGoogleApiClient();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.walkdata:
                putDistance();
                totalwalk = preference.getFloat(DataTAG, 0f);
                Toast.makeText(getApplicationContext(), "Total distance travelled is " + totalwalk + " meters", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear:
                SharedPreferences.Editor editor = preference.edit();
                editor.remove(ArrayTAG);
                editor.remove(DataTAG);
                editor.commit();
                line.remove();
                Toast.makeText(getApplicationContext(), "Data Cleared", Toast.LENGTH_SHORT).show();
                break;
            case R.id.refresh:
                locate();
                plotroute();
                Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();

                break;

        }


        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

        locate();

    }

    public void locate() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            final LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (TP != null)
                TP.remove();
            TP = googleMap.addMarker(new MarkerOptions().position(pos).title(
                    "Latitude:" + mLastLocation.getLatitude() + "\nLongitude:" + mLastLocation.getLongitude()));
            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pos)         // Sets the center of the map to my coordinates
                    .zoom(18)                   // Sets the zoom
                    .bearing(180)                // Sets the orientation of the camera to south
                    .tilt(30)                  // Sets the tilt of the camera to 30 degrees
                    .build();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    Set<String> set = preference.getStringSet(ArrayTAG, null);
                    if (set == null) {
                        set = new HashSet<String>();
                    }
                    String locationToAdd = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
                    set.add(locationToAdd);
                    plotroute();
                }
            }, 2000);


        } else {
            Toast.makeText(this, "No location detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    public void plotroute() {

        Set<String> set = preference.getStringSet(ArrayTAG, null);
        PolylineOptions options = new PolylineOptions();
        options.color(Color.RED);
        options.width(5);

        if (set != null) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String data = (String) iterator.next();
                String[] array = data.split(",");
                LatLng latlng = new LatLng(Double.parseDouble(array[0]), Double.parseDouble(array[1]));
                options.add(latlng);

            }

        }

        if (line != null) {
            line.remove();
        }
        line = googleMap.addPolyline(options);

    }

    public void putDistance() {
        totalwalk = 0;
        Set<String> set = preference.getStringSet(ArrayTAG, null);

        if (set != null) {
            Iterator iterator = set.iterator();
            float length = 0f;
            String prev = "";
            if (iterator.hasNext()) {
                prev = (String) iterator.next();
                // Toast.makeText(getApplicationContext(),prev,Toast.LENGTH_LONG).show();
            }
            while (iterator.hasNext()) {
                String current = (String) iterator.next();
                //Toast.makeText(getApplicationContext(),current,Toast.LENGTH_LONG).show();
                String[] currentarray = current.split(",");
                String[] prevarray = prev.split(",");
                Double prevlat = Double.parseDouble(prevarray[0]);
                Double prevlon = Double.parseDouble(prevarray[1]);

                Double currlat = Double.parseDouble(currentarray[0]);

                Double currlon = Double.parseDouble(currentarray[1]);
                Location locationA = new Location("point A");

                locationA.setLatitude(prevlat);
                locationA.setLongitude(prevlon);

                Location locationB = new Location("point B");

                locationB.setLatitude(currlat);
                locationB.setLongitude(currlon);

                float distance = locationA.distanceTo(locationB);
                totalwalk += distance;
                prev = current;

            }

            SharedPreferences.Editor editor = preference.edit();
            editor.putFloat(DataTAG, totalwalk);
            editor.commit();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {


            if (loc != null) {
                Set<String> set = preference.getStringSet(ArrayTAG, null);
                if (set == null) {
                    set = new HashSet<String>();
                }
                String locationToAdd = loc.getLatitude() + "," + loc.getLongitude();
                set.add(locationToAdd);
                SharedPreferences.Editor editor = preference.edit();
                editor.putStringSet(ArrayTAG, set);
                editor.commit();
                Toast.makeText(getApplicationContext(), "LocationUpdated", Toast.LENGTH_LONG).show();
                locate();
                plotroute();
                // Do something knowing the location changed by the distance you requested

            }
        }

        @Override
        public void onProviderDisabled(String arg0) {
            // Do something here if you would like to know when the provider is disabled by the user
        }

        @Override
        public void onProviderEnabled(String arg0) {
            // Do something here if you would like to know when the provider is enabled by the user
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // Do something here if you would like to know when the provider status changes
        }
    }

}