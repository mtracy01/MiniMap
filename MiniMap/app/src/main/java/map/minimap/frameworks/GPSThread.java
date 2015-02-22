package map.minimap.frameworks;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;

/**
 * Created by Corey on 2/17/2015.
 */
public class GPSThread {

    private map.minimap.MainActivity activity;
    private LocationListener locationListener;
    private static LocationManager locationManager;

    public GPSThread(map.minimap.MainActivity activity,final ServerConnection client) {
        this.activity =  activity;
        final int MINTIME = 1000;
        final int MINDIST = 1;

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location
                // provider.
                client.sendMessage("location" +location.getLatitude() + " " + location.getLongitude());
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDIST, locationListener);
    }
    public void destroyListener(){locationManager.removeUpdates(locationListener);}

}
