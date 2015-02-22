package map.minimap.helperClasses;
import android.app.Application;

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
    /*public static void setUser(User user2){
        user = user2;
    }
    public static User getUser(){
        return user;
    }

    public static void setClient(ServerConnection c){
        client = c;
    }
    public static ServerConnection getClient(){return client;}

    public static void setUserList(User[] userList){
        int length = userList.length;
        users = new User[length];
        System.arraycopy(userList,0,users,0,length);
    }
    public static User[] getUserList(){
        return users;
    }

    public static void setSession(Session session2){session = session2;}
    public static Session getSession(){return session;}*/
}
