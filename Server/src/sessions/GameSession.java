package sessions;

import java.util.ArrayList;
import java.util.logging.Logger;

import server.Server;
import server.Team;
import server.User;

public abstract class GameSession {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	private static Integer baseId = 0;
	
	/**
	 * The game session id
	 */
	private int id;

	/**
	 * The users in the game session
	 */
	protected ArrayList<User> users;
	
	/**
	 * The teams in the game session
	 */
	protected ArrayList<Team> teams;
	
	
	public GameSession() {
		// set the id of the game session
		synchronized (baseId) {
			id = baseId;
			baseId++;
		}
	}
	
	
	/**
	 * Handle an incoming message.  This is handled on a per game basis.
	 * @param message The incoming message.
	 * @param user The user who sent the message.
	 */
	public abstract void handleMessage(String message, User user);
	
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
}
