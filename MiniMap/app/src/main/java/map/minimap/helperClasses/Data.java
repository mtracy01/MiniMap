package map.minimap.helperClasses;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

import map.minimap.frameworks.GPSThread;
import map.minimap.frameworks.MapResources.SyncedMapFragment;
import map.minimap.frameworks.ServerConnection;
import map.minimap.frameworks.User;

/**
 * Created by Matthew on 2/21/2015.
 * Purpose: Store global data that is used throughout the application
 */
public class Data {
    //Things for the application to store
    public static ActionBarActivity mainAct;                               //The activity that is responsible for taking care of the GPS thread
    public static User user;                                     //The user that is using this application

    public static boolean host = false;
    public static String gameId;                                //The id of the application's current game session
    public static ServerConnection client;                     //The client side server communication object
    public static GoogleMap map;                              //The actual Google map displayed in the mapFragment when the game session is running
    public static SyncedMapFragment mapFragment;             //Custom mapFragment for maintaining asynchronous location updates
    public static Activity gameActivity;                    // A reference to the current game activity.  Used to display popups in game.
    public static GPSThread gps;                            //The client's current GPS thread
    public static int loggedInFlag=0;                      //Flag used to determine whether the person is already logged in when the app is reopened

    //Items used in Lobby fragment and game
    public static ArrayList<String> lobbyUsers     = new ArrayList<>();         //Users in the lobby currently
    public static ArrayList<User> invitableUsers = new ArrayList<>();        //Possible users that can be invited.  This is displayed in the invite dialog
    public static ArrayList<User> selectedUsers  = new ArrayList<>();       //Users selected to be invited in the invite dialog
    public static ArrayList<User> players        = new ArrayList<>();      //Players in the current game

    public static int clientDoneFlag = 0;                                 //Flag for syncing between tasks
    public static int initialized = 0;

    public static boolean assassinKillRange;
    public static User assassinateUser;
}
