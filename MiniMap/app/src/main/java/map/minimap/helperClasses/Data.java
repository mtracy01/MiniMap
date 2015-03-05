package map.minimap.helperClasses;
import android.app.Activity;

import com.facebook.Session;

import java.util.ArrayList;

import map.minimap.frameworks.ServerConnection;
import map.minimap.frameworks.User;

/**
 * Created by Matthew on 2/21/2015.
 * Purpose: Store global data that is used throughout the application
 */
public class Data {
    // this will be our controller
    //Things for the application to store
    public static Activity mainAct;
    public static User user = new User("abc123");
    public static ArrayList<User> users;
    public static String gameId;
    //Facebook session info
    //static Session session;
    public static ServerConnection client;
    public final static Object LOGIN_LOCK = new Object();
    public static Session session;

}
