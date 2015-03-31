package map.minimap.helperClasses;
import android.app.Activity;

import com.facebook.AccessToken;


import java.util.ArrayList;

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
    public static ArrayList<User> users;
    public static String gameId;
    public static AccessToken accessToken;
    //Facebook session info
    //static Session session;
    public static ServerConnection client;


    public static GoogleMap map;

}
