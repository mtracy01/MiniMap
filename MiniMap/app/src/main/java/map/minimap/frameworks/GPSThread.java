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

    public GPSThread(map.minimap.MainActivity activity) {
        this.activity =  activity;
        final ServerConnection client = new ServerConnection(activity);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location
                // provider.
                client.sendMessage(location.getLatitude() + "," + location.getLongitude());
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
    }


}
