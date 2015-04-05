package map.minimap.helperClasses;
import android.app.Activity;




import java.util.ArrayList;

import map.minimap.frameworks.GPSThread;
import map.minimap.frameworks.MapResources.SyncedMapFragment;
import map.minimap.frameworks.ServerConnection;
import map.minimap.frameworks.User;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Matthew on 2/21/2015.
 * Purpose: Store global data that is used throughout the application
 */
public class Data {
    // this will be our controller
    //Things for the application to store
    public static Activity mainAct;
    public static User user;// = new User("a");
    public static ArrayList<User> players;
    public static String gameId;
    public static ServerConnection client;
    public static GoogleMap map;
    public static SyncedMapFragment mapFragment;
    public static GPSThread gps;

}
