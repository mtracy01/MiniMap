package sessions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import server.Location;
import server.Server;
import server.User;
import server.Utility;

public class AssassinsSession extends GameSession {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	/**
	 * This stores which users are currently targeting each other
	 * <user, target>
	 */
	private HashMap<User, User> targets;
	
	/**
	 * Store any potential finds
	 */
	private HashSet<PotentialFind> potentialFinds;

	public AssassinsSession(User owner, Server server) {
		super("assassins", owner, server);
		
		targets = new HashMap<User, User>();
		potentialFinds = new HashSet<PotentialFind>();
	}

	@Override
	public void handleMessage(String message, User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLocation(Location loc, User user) {
		// Get the target of the user and see if their locations are close
		User target = targets.get(user);
		boolean close = Utility.areClose(user, target, Utility.PROXIMITY_DISTANCE);
		
		if (close) {
			// The users are close together and are not in the process of confirming,
			// start the process
			PotentialFind find = new PotentialFind();
			find.assassin = user;
			find.target = target;
			if (!potentialFinds.contains(find)) {
				potentialFinds.add(find);
				user.sendMessage("acceptKill " + target.getUserID());
				target.sendMessage("acceptDeath " + user.getUserID());
			}
		}

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

	/**
	 * A helper class used to store the status of potential finds
	 *
	 */
	class PotentialFind {
		public User assassin;
		public User target;
		public boolean assassinConfirm;
		public boolean targetConfirm;
		
		public boolean bothConfirmed() {
			return assassinConfirm && targetConfirm;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((assassin == null) ? 0 : assassin.hashCode());
			result = prime * result
					+ ((target == null) ? 0 : target.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PotentialFind other = (PotentialFind) obj;
			if (assassin == null) {
				if (other.assassin != null)
					return false;
			} else if (!assassin.equals(other.assassin))
				return false;
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (!target.equals(other.target))
				return false;
			return true;
		}
		
		
	}
	
}
