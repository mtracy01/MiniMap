package map.minimap.frameworks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import map.minimap.helperClasses.FacebookHelper;

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
     * A User has friends[], a name, an ID, and coordinates
     */

    private LatLng coordinates;


    private String name;
    private String ID;

    private int team;

    private boolean inGame;
    private Game currentGame;

    private Marker marker;  //Location object for GoogleMap
    private ArrayList<User> friends;
    private Bitmap profilePhoto;

    private ArrayList<Beacon> beacons;

    public User(String id) {

        coordinates = new LatLng(0,0);
        name = ""; //Needs to be specified later
        ID = id;
        team=1;
        friends = null; //Needs to be specified later
        inGame = false;
        currentGame = null;
        beacons = new ArrayList<Beacon>();

        AsyncTask<Void,Void,Void> profileRetriever = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                profilePhoto = FacebookHelper.getFacebookProfilePicture(ID);
                return null;
            }
        };
        profileRetriever.execute();
    }

    /* Getters and Setters */

    public LatLng getCoordinates(){return coordinates;}
    public void setCoordinates(LatLng newCoordinates){coordinates=newCoordinates;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() { return ID; }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) { this.friends = friends; }

    public void setMarker(Marker m){ marker = m; }
    public Marker getMarker(){ return marker;}

    public void setTeam(int t){ team = t; }
    public int getTeam(){ return team; }

    public boolean getInGame() { return inGame; }
    public void setInGame(boolean g) {
        inGame = g;
    }

    public Game getGame() {
        return currentGame;
    }
    public void setGame(Game g) { currentGame = g;}

    public Bitmap getProfilePhoto() {return profilePhoto;}
    public void setProfilePhoto(Bitmap profilePhoto){ this.profilePhoto = profilePhoto;}

    public User findUserById(String id) {
        for (User u: friends) {
            if (u.getID().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public void addBeacon(Beacon b)
    {
        beacons.add(b);
    }
    public boolean removeBeaconByID(int id)
    {
        for (Beacon b: beacons)
        {
            if (b.getBeaconID() == id) {
                b.removeBeacon();
                beacons.remove((b));
                return true;
            }
        }
        return false;
    }
}
