package sessions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import server.Location;
import server.Server;
import server.Team;
import server.User;

public abstract class GameSession {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	private static Integer baseId = 0;
	
	/**
	 * The game type
	 */
	private String gameType;
	
	/**
	 * The game session id
	 */
	private int id;
	
	/**
	 * The server
	 */
	protected Server server;
	
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
	protected Set<User> users;
	
	/**
	 * The teams in the game session
	 */
	protected ArrayList<Team> teams;
	
	
	
	public GameSession(String gameType, User owner, Server server) {
		// set the id of the game session
		synchronized (baseId) {
			id = baseId;
			baseId++;
		}
		this.users = new HashSet<User>();
		this.owner = owner;
		this.gameType = gameType;
		this.teams = new ArrayList<Team>();
		this.server = server;
		users.add(owner);
		isRunning = false;
	}
	
	/**
	 * Handle an incoming message.  This is handled on a per game basis.
	 * @param message The incoming message.
	 * @param user The user who sent the message.
	 */
	public abstract void handleMessage(String message, User user);
	
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

	/**
	 * Called when user accepts the invitation
	 * This also sends out an updated list of all users to every client
	 * @param user
	 */
	public void accept(User user) {
		synchronized (users) {
			users.add(user);
		}
		log.fine("users: " + users);
		user.setGameSession(this);
		user.setInGame(true);
		sendSessionUsers();
	}
	
	/**
	 * Called when user rejects the invitation
	 * @param user
	 */
	public void reject(User user) {
		sendSessionUsers();
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the owner
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @param isRunning the isRunning to set
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * Send a list of all users in the session to every user
	 */
	protected void sendSessionUsers() {
		synchronized (users) {
			StringBuilder usersMessage = new StringBuilder();
			usersMessage.append("gameUsers ");
			usersMessage.append(id);
			for (User u : users) {
				usersMessage.append(' ');
				usersMessage.append(u.getUserID());
			}
			
			String message = usersMessage.toString();
			log.fine("All users message: " + message);
			
			for (User u : users) {
				u.sendMessage(message);
			}
		}
	}
	
	/**
	 * Send the game start message to all users
	 */
	protected void sendStartMessage() {
		String message = "gameStart " + id;
		synchronized (users) {
			for (User u : users) {
				u.sendMessage(message);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + " [id=" + id + ", users=" + users + ", teams="
				+ teams + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameSession other = (GameSession) obj;
		if (id != other.id)
			return false;
		return true;
	}	
	
	/**
	 * Get a team by its id
	 * @param teamlist
	 * @param id
	 * @return
	 */
	public Team getTeambyID(ArrayList<Team> teamlist, int id) {
		for (Team t: teamlist) {
			if (t.getTeamID() == id) {
				return t;
			}
		}
		return null;
	}
	
	
}
