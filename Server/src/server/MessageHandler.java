package server;

import java.util.logging.Logger;

import sessions.AssassinsSession;
import sessions.FriendFinderSession;
import sessions.GameSession;
import sessions.SardinesSession;

/**
 * MessageHandler
 * Used to handle messages for a specific user
 * @author nickiogg
 *
 */
public class MessageHandler {
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	private Server server;
	private User user;
	
	public MessageHandler(Server server, User user) {
		this.server = server;
		this.user = user;
	}
	
	/**
	 * Handle an incoming message
	 * @param message
	 */
	public void handleMessage(String message) {
		String[] messageParts = message.split(" ");
		if (messageParts.length == 0) {
			return;
		}
		log.fine("message \"" + message + "\" received from user " + user);
		switch(messageParts[0]) {
			case "createGame":
				//createGame(messageParts[1]);
				
				/*
				 * Temporary (I hope) until invites work through facebook
				 */
				createGame(messageParts[1]);
				break;
			case "invite":
				// Make sure we are in a game and are not inviting to other games.
				if (!user.isInGame() || Integer.parseInt(messageParts[1]) != user.getGameSession().getId()) {
					break;
				}
				for (int i = 2; i < messageParts.length; i++) {
					User u = server.getUserByID(messageParts[i]);
					if (u != null) {
						log.finer("Inviting " + u.getUserID() + " to " + user.getGameSession().getId());
						u.sendMessage("invite " + getGameType(user.getGameSession()) + " " + user.getGameSession().getId());
					}
				}
				break;
			case "accept":
				GameSession sessionAccept = server.getSessionByID(Integer.parseInt(messageParts[1]));
				sessionAccept.accept(user);
				break;
			case "reject":
				GameSession sessionReject = server.getSessionByID(Integer.parseInt(messageParts[1]));
				sessionReject.reject(user);
				break;
			case "getAllUsers":
				server.sendAllUsers(user);
				break;
			case "start":
				GameSession sessionStart = server.getSessionByID(Integer.parseInt(messageParts[1]));
				// If the current user is the owner, start the session
				if (sessionStart.getOwner().equals(user)) {
					sessionStart.startSession();
				}
				break;
			case "addbeacon":
				Location locAdd = new Location(Double.parseDouble(messageParts[1]), Double.parseDouble(messageParts[2]));
				user.getGameSession().addBeacon(user.getTeamID(), locAdd);
				break;
			case "removebeacon":
				user.getGameSession().removeBeacon(user.getTeamID(), Integer.parseInt(messageParts[1]));
				break;
			case "remove":
				if (user.isInGame()) {
					// Check if we are the owner
					GameSession session = server.getSessionByID(Integer.parseInt(messageParts[1]));
					
					// If the current user is the owner, remove the user
					if (session.getOwner().equals(user)) {
						User toRemove = server.getUserByID(messageParts[2]);
						session.removeUser(toRemove);
					} else if (user.getUserID().equals(messageParts[2])) {
						// If the user wants to remove themselves, remove the user
						user.getGameSession().removeUser(user);
					}
				}
				break;
			case "location":
				Location loc = new Location(Double.parseDouble(messageParts[1]), Double.parseDouble(messageParts[2]));
				user.setLocation(loc);
				if (user.isInGame()) {
					user.getGameSession().handleLocation(loc, user);
				}
				break;

			// Database messages begin here
			/*case "addUserToDB":
				DbInteract db = new DbInteract();
				db.addUser(messageParts[1]);
				db.closeConnection();
				break;
			case "getGroupsByID":
				DbInteract db = new DbInteract();
				String[] groups = db.getGroupsByID(messageParts[1]);
				String groupString = DbInteract.convertToGroupString(groups);
				user.sendMessage(groupString);
				db.closeConnection();
				break;
			case "addGroup":
				DbInteract db = new DbInteract();
				db.addGroup(messageParts[1], messageParts[2]);
				db.closeConnection();
				break;
			case "removeAllUserGroups":
				DbInteract db = new DbInteract();
				db.removeGroup(messageParts[1], "*");
				db.closeConnection();
				break;
			case "removeUserGroup":
				DbInteract db = new DbInteract();
				db.removeGroup(messageParts[1], messageParts[2]);
				db.closeConnection();
				break;
			// Database messages end here
			*/

			default:
				// Bounce the message to the game session
				if (user.isInGame()) {
					user.getGameSession().handleMessage(message, user);
				}
				break;
		}
	}

	/**
	 * Create a game of the specified type
	 * @param gameType
	 */
	private void createGame(String gameType) {
		GameSession gameSession = null;
		switch (gameType) {
			case "friendFinder":
				gameSession = new FriendFinderSession(user, server);
				break;
			case "ctf":
				break;
			case "marcoPolo":
				break;
			case "sardines":
				gameSession = new SardinesSession(user, server);
				break;
			case "slender":
				break;
			case "assassins":
				gameSession = new AssassinsSession(user, server);
				break;
		}
		if (gameSession != null) {
			server.addSession(gameSession);
		}
		// Send the id back to the client
		user.setGameSession(gameSession);
		user.setInGame(true);
		user.sendMessage("game " + gameSession.getId());
	}
	
	/**
	 * Get the type of a game.
	 * Returns:
	 * 	friendFinder
	 * 	ctf
	 * 	marcoPolo
	 * 	sardines
	 * 	slender
	 *  assassins
	 * @param session
	 * @return
	 */
	private String getGameType(GameSession session) {
		if (session instanceof FriendFinderSession) {
			return "friendFinder";
		} else if (session instanceof FriendFinderSession) {
			return "ctf";
		} else if (session instanceof FriendFinderSession) {
			return "marcoPolo";
		} else if (session instanceof SardinesSession) {
			return "sardines";
		} else if (session instanceof FriendFinderSession) {
			return "slender";
		} else if (session instanceof AssassinsSession) {
			return "assassins";
		}
		return null;
	}
}
