package map.minimap.frameworks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.net.URL;

import map.minimap.R;

/**
 * Created by Joe Coy on 2/20/2015.
 */
public class User {
    /*
     * User has a client ID and Friends
     * User can sendMessage(message) and handleMessage(message)
     *
     * A User has exactly one server
     * A User has 0 to many teams (?)
     * A User has friends[], a name, an ID, Xcoord, and Ycoord
     */
    private double Xcoord, Ycoord;

    //Debug tag for logging errors in LogCat
    private String LOG_TAG="User";

    private String name;
    private String ID;

    //The affiliated team of the player.  0 by default.
    private int team;

    private Marker marker;  //Location object for GoogleMap
    private User friends[];

    public User(String id) {
        Xcoord = 0;
        Ycoord = 0;
        name = ""; //Needs to be specified later
        ID = id;
        team=0;
        friends = null; //Needs to be specified later

    }

    /* Getters and Setters */

    public double getXcoord() {
        return Xcoord;
    }

    public double getYcoord() {
        return Ycoord;
    }

    public void setXcoord(double xcoord) {
        Xcoord = xcoord;
    }

    public void setYcoord(double ycoord) {
        Ycoord = ycoord;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public User[] getFriends() {
        return friends;
    }

    public void setFriends(User[] friends) {
        this.friends = friends;
    }

    public void setMarker(Marker m){ marker = m; }
    public Marker getMarker(){ return marker;}

    public void setTeam(int t){ team = t; }
    public int getTeam(){ return team; }


    //Return the user image
    public Bitmap getUserImage(){
        Bitmap userIcon = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_4444);
        try{
            URL img_value = null;
            img_value = new URL("http://graph.facebook.com/"+getID()+"/picture?type=small");
            userIcon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
        }
        catch(Exception e){
            Log.e(LOG_TAG, "Failed to get User image" + e.getMessage());

        }
        return userIcon;
    }
}
