package com.example.bhaskar.locationtest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by bhaskar on 29/3/15.
 */
public class MainService extends Service

{

    private final String URL="http://ves.hol.es/mcc.php";
    private LocationManager locationManager;

    public MainService() {
        // TODO Auto-generated constructor stub
        //Toast.makeText(getApplicationContext(),"mainserrvice constructor",Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        //Toast.makeText(getApplicationContext(),"onbindcalled",Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //Toast.makeText(getApplicationContext(),"main service oncreate",Toast.LENGTH_LONG).show();

        // Toast.makeText(getApplicationContext(), "Service Created",
        // Toast.LENGTH_SHORT).show();

        Log.e("Google", "Service Created");

    }

    public void sendata(Location location,boolean first) {
       // Toast.makeText(getApplicationContext(),"Inside send data",Toast.LENGTH_LONG).show();
        if (isConnectedToInternet(this)) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            String sendloc = location.getLatitude() + "," + location.getLongitude();
            params.put("Location", sendloc);
            SharedPreferences preferences = getSharedPreferences("locationtest", MODE_PRIVATE);

            String username=preferences.getString("username","");
            //Toast.makeText(getApplicationContext(),"username="+username,Toast.LENGTH_SHORT).show();
            params.put("UserName", username);
            client.post(URL, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                   // Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    //Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                   // Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onRetry(int retryNo) {
                    //Toast.makeText(getApplicationContext(),"Retrying",Toast.LENGTH_LONG).show();
                }
            });

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       // Toast.makeText(getApplicationContext(),"Inside on StartCommand",Toast.LENGTH_LONG).show();
        Log.e("Google", "Service Started");

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 5, listener);

        return super.onStartCommand(intent, flags, startId);

    }

private LocationListener listener = new LocationListener() {

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        Log.e("Google", "Location Changed");
        Toast.makeText(getApplicationContext(),"LocationChanged",Toast.LENGTH_LONG).show();
        sendata(location,false);

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
};

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();


    }

    public static boolean isConnectedToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

}
