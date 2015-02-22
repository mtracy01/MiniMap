package server;

import java.util.logging.Level;
import java.util.logging.Logger;

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
		log.log(Level.FINE, "message \"{0}\" received", message);
		switch(messageParts[0]) {
			case "createGame":
				break;
			case "accept":
				break;
			case "reject":
				break;
			case "location":
				break;
			case "getAllUsers":
				server.sendAllUsers(user);
				break;
		}
	}
}
