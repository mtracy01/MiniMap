package map.minimap.frameworks.gameResources;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Hashtable;

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

    private String ID;                //id of the user
    private String name;             //name of the user
    private LatLng coordinates;     //location of the user

    private int team;

    private boolean inGame;
    private Game currentGame;

    private String groups;

    private Marker marker;  //Location object for GoogleMap
    private ArrayList<User> friends;
    private Bitmap profilePhoto;

    private ArrayList<Beacon> beacons;

    //Structures for optimized procedures
    private Hashtable<String,User> friendMap = new Hashtable<>();           //Mapping of User ID strings to the User object
    private Hashtable<Integer,Beacon> beaconMap = new Hashtable<>();       //Mapping of Beacon ID to index in Beacon ArrayList (since removal requires it)

    public User(String id) {

        coordinates = new LatLng(0,0);
        name = ""; //Needs to be specified later
        ID = id;
        team=1;
        friends = null; //Needs to be specified later
        inGame = false;
        currentGame = null;
        groups = null;
        beacons = new ArrayList<>();
        profilePhoto = null;

        // Name retrieval task
        AsyncTask<String,Void,Void> nameRetriever = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                name = FacebookHelper.getFacebookName(params[0]);
                return null;
            }
        };
        nameRetriever.execute(ID);

        // Profile picture retrieval task
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getID() { return ID; }

    public ArrayList<User> getFriends() { return friends; }
    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
        /*
         * Set up hashtable for fast list intersections of
         * friends lists with server info
         * result: O(1) friend lookups
         */
        friendMap.clear();
        for(User u: friends)
            friendMap.put(u.getID(), u);
    }

    public Marker getMarker(){ return marker;}
    public void setMarker(Marker m){ marker = m; }

    public int getTeam(){ return team; }
    public void setTeam(int t){ team = t; }

    public String getGroups() {return groups;}
    public void setGroups(String groups) {this.groups = groups;}

    public boolean getInGame() { return inGame; }
    public void setInGame(boolean g) { inGame = g; }

    public Game getGame() { return currentGame; }
    public void setGame(Game g) { currentGame = g;}

    public Bitmap getProfilePhoto() {return profilePhoto;}
    public void setProfilePhoto(Bitmap profilePhoto){ this.profilePhoto = profilePhoto;}

    public User findUserById(String id) { return friendMap.get(id); }

    public void addBeacon(Beacon b)
    {
        beacons.add(b);
        beaconMap.put(b.getBeaconID(),b);
    }
    public boolean removeBeaconByID(int id)
    {
        Beacon beacon = beaconMap.get(id);
        //if the beacon does not exist, return false
        if(beacon == null) {
            return false;
        }
        //beacon does exist, remove it from map and ArrayList
        beacon.removeBeacon();
        beacons.remove(beacon);
        beaconMap.remove(id);
        return true;
    }

    public ArrayList<Beacon> getBeacons() {
        return beacons;
    }
}
