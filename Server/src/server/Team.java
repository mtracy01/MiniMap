package server;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Team {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	/**
	 * Store the users in the team
	 */
	private ArrayList<User> users;
	private static Integer teamID = 0;
	private int id;
	private ArrayList<Beacon> beacons;
	
	public Team() {
		users = new ArrayList<User>();
		synchronized (teamID) {
			this.teamID = id;
			teamID++;
		}
		beacons = new ArrayList<Beacon>();
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
