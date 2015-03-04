package games;

import java.util.ArrayList;
import java.util.logging.Logger;

import server.Location;
import server.Server;
import server.Team;
import server.User;

/**
 * Created by joe on 2/21/2015.
 */
public abstract class Game {

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
	
	public int getId() {
		return id;
	}
	
	
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
	 * The users in the game session
	 */
	protected ArrayList<User> users;
	
	/**
	 * The teams in the game session
	 */
	protected ArrayList<Team> teams;
	
	
    public Game() {}
    public abstract void processLogic ();
    public abstract void handleMessage();
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
	public abstract void addBeacon(int teamid, Location loc);
	
	/**
	 * Remove the specified beacon
	 * @param id
	 */
	public abstract void removeBeacon(int teamid, Integer id);
    
	public Team getTeambyID(ArrayList<Team> teamlist, int id) {
		for (Team t: teamlist) {
			if (t.getTeamID() == id) {
				return t;
			}
		}
		return null;
	}
	
	
    
    
    
    
    
    
    

}
