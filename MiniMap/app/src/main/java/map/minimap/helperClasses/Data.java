package map.minimap.helperClasses;
import com.facebook.Session;

import map.minimap.frameworks.ServerConnection;
import map.minimap.frameworks.User;

/**
 * Created by Matthew on 2/21/2015.
 * Purpose: Store global data that is used throughout the application
 */
public class Data {
    // this will be our controller
    //Things for the application to store

    public static User user = new User("abc123");
    public static User[] users;

    //Facebook session info
    //static Session session;
    public static ServerConnection client;
    public static Session session;

}
