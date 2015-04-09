package server;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Team {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	/**
	 * Store the users in the team
	 */
	private ArrayList<User> users;
	private int teamID;
	private int id;
	private ArrayList<Beacon> beacons;
	
	public Team(int tid) {
		users = new ArrayList<User>();
		this.teamID = tid;
		beacons = new ArrayList<Beacon>();
	}
	
	/**
	 * Add a user to the team
	 * @param u
	 */
	public void addUser(User u) {
		synchronized (users) {
			users.add(u);
			u.setTeamID(this.teamID);
		}
	}
	
	public void addBeacon(Beacon b) {
		synchronized (beacons) {
			beacons.add(b);
		}
	}
	
	/**
	 * get list of users 
	 */
	public ArrayList<User> getUsers()
	{
		return users;
	}
	
	/**
	 * returns true if user is in the team 
	 */
	public boolean contains(User user)
	{
		boolean found = false;
		synchronized (users) {
			for (User u: users)
			{
				if (u.equals(user))
				{
					found = true;
				}
			}
		}
		return found;
	}
	
	public Beacon getBeaconbyID(int id) {
		Beacon toReturn = null;
		synchronized (beacons) {
			for (Beacon b: beacons) {
				if (b.getId() == id) {
					toReturn = b;
				}
			}
		}
		return toReturn;
	}
	
	/**
	 * Remove a user from the team
	 * @param u
	 */
	public void removeUser(User u) {
		synchronized (users) {
			users.remove(u);
		}
	}
	
	public void removeBeacon(Beacon b) {
		synchronized (beacons) {
			beacons.remove(b);
		}
	}
	
	
	public void sendMessage(String message) {
		synchronized (users) {
			for (User u : users) {
				u.sendMessage(message);
			}
		}
	}
	
	public int getTeamID() {
		return teamID;
	}
	
	//Added for endSession: Can't access list of beacons otherwise to delete them all
	public void removeAllBeacons()
	{
		synchronized (beacons) {
			beacons.clear();
		}
	}
	
}
