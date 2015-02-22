package server;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Team {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	/**
	 * Store the users in the team
	 */
	private ArrayList<User> users;
	
	public Team() {
		users = new ArrayList<User>();
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
	
	/**
	 * Remove a user from the team
	 * @param u
	 */
	public void removeUser(User u) {
		synchronized (users) {
			users.remove(u);
		}
	}
	
	public void sendMessage(String message) {
		for (User u : users) {
			u.sendMessage(message);
		}
	}
}
