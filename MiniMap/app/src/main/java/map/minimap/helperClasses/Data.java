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
    //Things for the application to store
    public static Activity mainAct;
    public static User user;

    public static String gameId;
    public static ServerConnection client;
    public static GoogleMap map;
    public static SyncedMapFragment mapFragment;
    public static GPSThread gps;
    public static int loggedInFlag=0;

    public static ArrayList<User> lobbyUsers     = new ArrayList<>();         //Users in the lobby currently
    public static ArrayList<User> invitableUsers = new ArrayList<>();        //Possible users that can be invited.  This is displayed in the invite dialog
    public static ArrayList<User> selectedUsers  = new ArrayList<>();       //Users selected to be invited in the invite dialog
    public static ArrayList<User> players        = new ArrayList<>();      //Players in the current game

    public static int clientDoneFlag = 0;                                 //Flag for syncing between tasks
}
