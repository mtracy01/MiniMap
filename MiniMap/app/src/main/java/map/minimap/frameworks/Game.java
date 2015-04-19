package map.minimap.frameworks;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by joe on 2/21/2015.
 */
public abstract class Game extends Application{



    /*
     *   Each specific game type will be a subclass of Game
     *   Game will not actually provide much except for a means of
     *      refering to any game type as a Game
     */


	private static Integer baseId = 0;

	/**
	 * The game type
	 */
	private String gameType;

	/**
	 * The game session id
	 */
	protected int id;

    /**
     * Does this game mode support beacons?
     */
	protected boolean beaconsEnabled = false;

    public enum BeaconMode {
        NOTHING, ADD, REMOVE
    }

    protected BeaconMode beaconMode = BeaconMode.NOTHING;
	
	/**
	 * The server
	 */
//	protected Server server;
	
	/**
	 * The owner of the game session (the person who started it)
	 */
	protected User owner;
	
	/**
	 * Is the session running?
	 */
	protected boolean isRunning;
	
	/**
	 * The teams in the game session
	 */
	protected ArrayList<Team> teams;
	
    public abstract void processLogic ();
    public abstract void handleMessage(String message);
	/**
	 * Start the session
	 */
	public abstract void startSession();
	
	/**
	 * End the current session.
	 * Perform any necessary cleanup.
	 */
	public abstract void endSession();
	
	/**
	 * Remove a user from the game session
	 * @param user The user to remove
	 */
	public abstract void removeUser(User user);

	/**
	 * Add a user to the game session
	 * @param user
	 */
	public abstract void addUser(User user, int teamid);
	
	/**
	 * Add a beacon at the specified location
	 * @param loc
	 */
	public abstract void addBeacon(int teamid, LatLng loc);
	
	/**
	 * Remove the specified beacon
	 * @param id
	 */
	public abstract void removeBeacon(Integer id);
    
	public Team getTeambyID(ArrayList<Team> teamlist, int id) {
		for (Team t: teamlist) {
			if (t.getTeamID() == id) {
				return t;
			}
		}
		return null;
	}

    public Team getUserTeam(User u) {
        for (Team t : teams) {
            if (t.containsUser(u)) {
                return t;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public boolean isBeaconsEnabled() {
        return beaconsEnabled;
    }

    public void setBeaconsEnabled(boolean beaconsEnabled) {
        this.beaconsEnabled = beaconsEnabled;
    }


    public BeaconMode getBeaconMode() {
        return beaconMode;
    }

    public void setBeaconMode(BeaconMode beaconMode) {
        this.beaconMode = beaconMode;
    }
}
