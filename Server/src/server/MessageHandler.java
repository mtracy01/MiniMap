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
	
	public void handleMessage(String message) {
		String[] messageParts = message.split(" ");
		if (messageParts.length == 0) {
			return;
		}
		log.log(Level.FINE, "message \"{0}\" received from user {1}", new Object[]{message, user.getId()});
		switch(messageParts[0]) {
			case "createGame":
				ArrayList<User> users = new ArrayList<User>();
				users.add(user);
				for (int i = 2; i < messageParts.length; i++) {
					User u = server.getUserByID(messageParts[i]);
					// The user could be null if they disconnected when we received the message
					if (u != null) {
						users.add(u);
					}
				}
				createGame(messageParts[1], users);
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

	private void createGame(String gameType, ArrayList<User> users) {
		GameSession gameSession = null;
		switch (gameType) {
			case "friendFinder":
				gameSession = new FriendFinderSession(users);
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
			gameSession.sendInvites();
		}
	}
}
