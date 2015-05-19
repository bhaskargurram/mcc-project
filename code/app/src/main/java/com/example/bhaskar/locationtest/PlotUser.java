package com.example.bhaskar.locationtest;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by bhaskar on 11/4/15.
 */
public class PlotUser extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleMap googleMap;
    protected GoogleApiClient mGoogleApiClient;
    String location;
    Double latitude,longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        location=extras.getString("location");
        //Toast.makeText(getApplicationContext(),location,Toast.LENGTH_SHORT).show();

        String[] latlong=location.split(",");
        latitude=Double.parseDouble(latlong[0]);
        longitude=Double.parseDouble(latlong[1]);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setBuildingsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //Toast.makeText(getApplicationContext(),"inside build",Toast.LENGTH_SHORT).show();
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
    @Override
    public void onConnected(Bundle bundle) {
        locate();
    }

    private void locate() {
        //Toast.makeText(getApplicationContext(),"Latitude="+latitude+"long="+longitude,Toast.LENGTH_SHORT).show();
        Location targetLocation = new Location("");//provider name is unecessary
        targetLocation.setLatitude(latitude);//your coords of course
        targetLocation.setLongitude(longitude);
        final LatLng pos = new LatLng(targetLocation.getLatitude(), targetLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(pos).title(
                "Latitude:" + targetLocation.getLatitude() + "\nLongitude:" + targetLocation.getLongitude()));
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

            }
        }, 2000);
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i("", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }
}