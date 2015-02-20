package map.minimap.frameworks;

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

    private String name;

    private String ID;

    private User friends[];

    public User(String id) {
        Xcoord = 0;
        Ycoord = 0;
        name = ""; //Needs to be specified later
        ID = id;
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
}
