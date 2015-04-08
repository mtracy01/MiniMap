package sessions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import server.Location;
import server.Server;
import server.Team;
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
		String[] parts = message.split(" ");
		if (parts[0].equals("confirmDeath")) {
			synchronized (users) {
				// Get the potentialFind for the user
				PotentialFind find = null;
				for (PotentialFind f : potentialFinds) {
					if (f.target.equals(user)) {
						find = f;
						break;
					}
				}
				if (find == null) {
					return;
				}
				
				if (parts[1].equals("true")) {
					// The target confirmed
					find.targetConfirm = true;
					if (find.bothConfirmed()) {
						processKill(find);
					}
				} else {
					// Reject happened, remove any potential find
					potentialFinds.remove(find);
				}
			}
		} else if (parts[0].equals("confirmKill")) {
			synchronized (users) {
				// Get the potentialFind for the user
				PotentialFind find = null;
				for (PotentialFind f : potentialFinds) {
					if (f.assassin.equals(user)) {
						find = f;
						break;
					}
				}
				if (find == null) {
					return;
				}
				
				
				if (parts[1].equals("true")) {
					// The assassin confirmed
					find.assassinConfirm = true;
					if (find.bothConfirmed()) {
						processKill(find);
					}
				} else {
					// Reject happened, remove any potential find
					potentialFinds.remove(find);
				}
			}
		}
	}

	/**
	 * Process a kill
	 * @param find
	 */
	private void processKill(PotentialFind find) {
		// Send out the global kill message
		String killMessage = "kill " + find.assassin.getUserID() + " " + find.target.getUserID();
		for (User u : users) {
			u.sendMessage(killMessage);
		}
		
		potentialFinds.remove(find);
		
		// Remove a potential find where the target is the assassin
		PotentialFind toRemove = null;
		for (PotentialFind f : potentialFinds) {
			if (f.assassin.equals(find.target)) {
				toRemove = f;
				break;
			}
		}
		if (toRemove != null) {
			potentialFinds.remove(toRemove);
		}
		
		// Set the new target for the assassin
		targets.put(find.assassin, targets.get(find.target));
		targets.remove(find.target);
		
		// If we start targeting ourselves, the game is over, end it
		if (find.assassin.equals(targets.get(find.assassin))) {
			endSession();
		}
		
		String targetMessage = "target " + targets.get(find.assassin).getUserID();
		find.assassin.sendMessage(targetMessage);
	}
	
	@Override
	public void handleLocation(Location loc, User user) {
		
		// Update the locations of the relevant clients
		String locationMessage = "location " + user.getUserID() + " " + loc.getLatitude() + " " + loc.getLongitude();
		for (Entry<User, User> entry : targets.entrySet()) {
			if (entry.getValue().equals(user)) {
				entry.getKey().sendMessage(locationMessage);
			}
		}
		user.sendMessage(locationMessage);
		
		// Get the target of the user and see if their locations are close
		User target = targets.get(user);
		// If the user has no target, don't process it
		if (target == null) {
			return;
		}
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
			// Only assign targets if there is more than 1 person in the game
			if (users.size() >= 2) {
				
				// Assign each user a target
				Object[] usersArray = users.toArray();
				for (int i = 0; i < usersArray.length - 1; i++) {
					targets.put((User) usersArray[i], (User) usersArray[i+1]);
				}
				targets.put((User) usersArray[usersArray.length - 1], (User) usersArray[0]);
			
				// Send the start message
				sendStartMessage();
			
				// Send target assignments
				for (Entry<User, User> entry : targets.entrySet()) {
					String message = "target " + entry.getValue().getUserID();
					entry.getKey().sendMessage(message);
				}
			} else {
				// Just one person, start with no targets
				sendStartMessage();
			}
		}
	}

	@Override
	public void endSession() {
		isRunning = false;
		
		synchronized (users) {
			Object[] userArray = users.toArray();
			for (Object u : userArray) {
				removeUser((User) u);
			}
		}
		
		// Remove ourselves
		server.removeSession(this);
	}

	@Override
	public void removeUser(User user) {
		log.finer("Removing user " + user + " from friendfinder session");
		
		
		user.setInGame(false);
		user.setGameSession(null);
		synchronized (users) {
			log.finer(users.toString());
			
			// Process someone leaving as if they were killed
			PotentialFind toRemove = null;
			for (Entry<User, User> entry : targets.entrySet()) {
				if (entry.getValue().equals(user)) {
					PotentialFind find = new PotentialFind();
					find.assassin = entry.getKey();
					find.target = user;
					if (!find.assassin.equals(find.target)) {
						toRemove = find;
					}
				}
			}
			if (toRemove != null) {
				log.fine("Removing find: " + toRemove.assassin.getUserID() + " -> " + toRemove.target.getUserID());
				processKill(toRemove);
			}
			
			
			// Actually remove the user
			users.remove(user);
			
			// Send the remove message to all users, including the one getting removed
			String removeMessage = "userRemoved " + user.getUserID();
			for (User u : users) {
				u.sendMessage(removeMessage);
			}
			user.sendMessage(removeMessage);
			
			log.finer(users.toString());
			log.finer(users.size() + " users in session");
			
			// Check for empty sessions
			if (users.isEmpty()) {
				endSession();
			}
			// Check for owner succession
			if (owner.equals(user) && !users.isEmpty()) {
				owner = users.iterator().next();
			}
		}
		sendSessionUsers();
	}

	@Override
	public void addUser(User user, int teamid) {
		// Don't do anything when a user is added beyond adding them to the user list
		synchronized (users) {
			users.add(user);
		}
		sendSessionUsers();
	}

	@Override
	public void addBeacon(int teamid, Location loc) {
		// Beacons are not part of the game

	}

	@Override
	public void removeBeacon(int teamid, Integer id) {
		// Beacons are not part of the game

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
