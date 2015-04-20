package map.minimap.frameworks.coreResources;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import map.minimap.frameworks.coreResources.ServerConnection;
import map.minimap.helperClasses.Data;

/**
 * Created by Corey on 2/17/2015.
 */
public class GPSThread {

    private LocationListener locationListener;
    private static LocationManager locationManager;
    private Location location;

    public GPSThread(final ServerConnection client) {
        final int MINTIME = 1000;
        final int MINDIST = 0;
         locationManager =(LocationManager) Data.mainAct.getSystemService(Data.mainAct.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.v("locs", Double.toString(location.getLatitude()));
                client.sendMessage("location " +location.getLatitude() + " " + location.getLongitude());
            }

            @Override
            public void onProviderDisabled(String provider) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME,
                    MINDIST, locationListener);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Log.v("locsG", location.toString());
                    client.sendMessage("location " + location.getLatitude() + " " + location.getLongitude());
                    Data.user.setCoordinates(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        }
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINTIME,
                    MINDIST, locationListener);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    Log.v("locsN", location.toString());
                    client.sendMessage("location " + location.getLatitude() + " " + location.getLongitude());
                    Data.user.setCoordinates(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        }
        else
            Log.v("gpsbroke", "network/gps providers both out");
    }
    public void destroyListener(){locationManager.removeUpdates(locationListener);}

}
