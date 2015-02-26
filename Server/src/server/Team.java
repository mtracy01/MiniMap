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
	private ArrayList<Beacon> beacons;
	
	public Team(int id) {
		users = new ArrayList<User>();
		this.teamID = id;
	}
	
	/**
	 * Add a user to the team
	 * @param u
	 */
	public void addUser(User u) {
		synchronized (users) {
			users.add(u);
		}
	}
	
	public void addBeacon(Beacon b) {
		synchronized (beacons) {
			beacons.add(b);
		}
	}
	
	public Beacon getBeaconbyID(int id) {
		for (Beacon b: beacons) {
			if (b.getId() == id) {
				return b;
			}
		}
		return null;
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
		for (User u : users) {
			u.sendMessage(message);
		}
	}
	
	public int getTeamID() {
		return teamID;
	}
	
}
