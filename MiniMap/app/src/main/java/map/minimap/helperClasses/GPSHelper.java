package map.minimap.helperClasses;

import android.util.Log;

import map.minimap.frameworks.coreResources.GPSThread;

/**
 * Created by Matthew on 5/12/2015.
 */
public class GPSHelper {

    private static String LOG_TAG = "GPSHelper";

    //Start GPS thread if it does not already exist
    public static void startGPSThread() {
        if (Data.gps == null && Data.client != null) {
            Data.gps = new GPSThread(Data.client);
            Log.v(LOG_TAG, "Created GPSThread");
        }
    }

    //Kill GPS thread if it exists
    public static void killGPSThread() {
        if (Data.gps != null) {
            Data.gps.destroyListener();
            Data.gps = null;
            Log.v(LOG_TAG, "Killed GPS Thread");

        }
    }

}
