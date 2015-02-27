package server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import sessions.FriendFinderSession;
import sessions.GameSession;

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
				createGame(messageParts[1]);
				break;
			case "accept":
				GameSession sessionAccept = server.getSessionByID(Integer.parseInt(messageParts[1]));
				sessionAccept.accept(user);
				break;
			case "reject":
				GameSession sessionReject = server.getSessionByID(Integer.parseInt(messageParts[1]));
				sessionReject.reject(user);
				break;
			case "location":
				break;
			case "getAllUsers":
				server.sendAllUsers(user);
				break;
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
				break;
			case "slender":
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
}
