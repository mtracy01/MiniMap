package sessions;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import server.Location;
import server.Server;
import server.User;

public class AssassinsSession extends GameSession {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	private HashMap<User, User> targets;

	public AssassinsSession(User owner, Server server) {
		super("assassins", owner, server);
		
		targets = new HashMap<User, User>();
	}

	@Override
	public void handleMessage(String message, User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLocation(Location loc, User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startSession() {
		log.fine("Starting game session " + this.getId());
		isRunning = true;
		
		synchronized (users) {
			// Assign each user a target
			User[] usersArray = (User[]) users.toArray();
			for (int i = 0; i < usersArray.length - 1; i++) {
				targets.put(usersArray[i], usersArray[i+1]);
			}
			targets.put(usersArray[usersArray.length - 1], usersArray[0]);
		}
		
		// Send the start message
		sendStartMessage();
		
		// Send target assignments
		synchronized (users) {
			for (Entry<User, User> entry : targets.entrySet()) {
				String message = "target " + entry.getValue().getUserID();
				entry.getKey().sendMessage(message);
			}
		}
	}

	@Override
	public void endSession() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUser(User user, int teamid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBeacon(int teamid, Location loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeBeacon(int teamid, Integer id) {
		// TODO Auto-generated method stub

	}

}
