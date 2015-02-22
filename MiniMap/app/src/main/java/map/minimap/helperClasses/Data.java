package map.minimap.helperClasses;
import android.app.Application;

import map.minimap.frameworks.User;

/**
 * Created by Matthew on 2/21/2015.
 * Purpose: Store global data that is used throughout the application
 */
public class Data extends Application {

    //Things for the application to store
    private User user;
    private User[] users;

    public void setUser(User user){
        this.user = user;
    }
    public User getUser(){
        return user;
    }

    public void setUserList(User[] userList){
        int length = userList.length;
        this.users = new User[length];
        System.arraycopy(userList,0,this.users,0,length);
    }

    public User[] getUserList(){
        return users;
    }

}
