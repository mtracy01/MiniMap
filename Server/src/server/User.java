package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import sessions.GameSession;
import database.DbInteract;

public class User extends Thread {

	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	private Server server;
	private Socket socket;
	
	// Input/output
	private PrintWriter out;
	private Scanner in;
	
	// Are we connected to the client?
	private boolean connected;
	
	/**
	 * A message handler for the user
	 */
	private MessageHandler messageHandler;
	
	
	// User information
	/**
	 * The userID
	 */
	private String userID;

    /**
     * The list of groups
     */
    private String groups;

	/**
	 * The teamID
	 */
	private int teamID;
	
	/**
	 * The current game session
	 */
	private GameSession gameSession;
	/**
	 * Are we in a game?
	 */
	private boolean inGame;
	/**
	 * The users last seen location
	 */
	private Location location;
	
	/**
	 * The time of the last heartbeat
	 */
	private long lastHeartBeat;

    /**
     * Object allowing interaction with database
     */
    private DbInteract db;

	public User(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		connected = false;
		location = null;
        groups = null;
	}
	
	/**
	 * Open the input and output streams.  Also set the user id.
	 */
	@Override
	public void run() {
		try {
			// Create a new PrintWriter with auto flush on
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new Scanner(socket.getInputStream());
		} catch (IOException e) {
			// Something went wrong
			log.log(Level.SEVERE, e.toString(), e);
			e.printStackTrace();
			// Cannot communicate, try closing socket
			try {
				socket.close();
			} catch (IOException e1) {
				log.log(Level.SEVERE, e1.toString(), e1);
				e1.printStackTrace();
			}
			// Return
			return;
		}
		
		// Get the user id
		String userIDLine = in.nextLine();
		boolean connectFail = true;
		if (userIDLine != null) {
			String[] lineParts = userIDLine.split(" ");
			if (lineParts.length == 2 && lineParts[0].equals("id")) {
				// We have the id
				userID = lineParts[1].trim();
				connectFail = false;
			}
		}
		
		if (connectFail) {
			// The connection failed due to no user id, close the socket
			closeSocket();
			return;
		}
		
		// We are connected
		connected = true;
		// We add the client to the server here because we know we can read/write from it
		boolean added = server.addUser(this);
		if (!added) {
			log.log(Level.WARNING, "User not added");
			connected = false;
			return;
		}
		
		// connect to database, attempt to add user to database
		db = new DbInteract();
        db.addUser(userID);
        db.closeConnection();

		// Create the message handler
		messageHandler = new MessageHandler(server, this);
		
		// Start listening for messages
		try {
			String line = null;
			while(in.hasNextLine()) {
				line = in.nextLine();
				try {
					handleMessage(line);
				} catch (Exception e) {
					log.warning("Exception handling a messagae: " + line);
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// When the socket is closed, an exception may be thrown
			// We only care about exceptions when we are still connected
			if (connected) {
				log.log(Level.SEVERE, e.toString(), e);
				e.printStackTrace();
			}
		}
		
		closeSocket();
	}
	
	
	/**
	 * Deal with an incoming message
	 * @param message
	 */
	private void handleMessage(String message) {
		log.fine(socket.getRemoteSocketAddress() + ": " + message);
		try {
			// For code cleanup
			messageHandler.handleMessage(message);
		} catch (Exception e) {
			// Something went wrong, don't crash
			System.err.println("Error handling message: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a message to the client
	 * @param message
	 */
	public void sendMessage(String message) {
		log.fine("Sending message \"" + message + "\" to " + userID);
		if (connected) {
			out.println(message);
		} else {
			log.log(Level.WARNING, "Attempting to send message \"{0}\" to disconnected user", message);
		}
	}
	
	/**
	 * Close the socket and any input/output streams
	 */
	public void closeSocket() {
		// Prevent double closings
		if (!connected) {
			return;
		}
		
		// Remove user from game session
		if (inGame) {
			gameSession.removeUser(this);
			gameSession = null;
			inGame = false;
		}
		
		// We are no longer connected
		connected = false;
		// Remove the client from the server
		server.removeUser(this);
		
		// Close input/output
		if (out != null) {
			out.close();
			out = null;
		}
		if (in != null) {
			in.close();
			in = null;
		}
		
		// Close the socket
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, e.toString(), e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @return the inGame
	 */
	public boolean isInGame() {
		return inGame;
	}

	/**
	 * @param inGame the inGame to set
	 */
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

    /**
     * @return the list of groups
     */
    public String getGroups() { return groups;}

    /**
     * set the list of groups
     */
    public void setGroups(String groups) {this.groups = groups;}
	/**
	 * @return the gameSession
	 */
	public GameSession getGameSession() {
		return gameSession;
	}

	/**
	 * @param gameSession the gameSession to set
	 */
	public void setGameSession(GameSession gameSession) {
		this.gameSession = gameSession;
	}
	
	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the lastHeartBeat
	 */
	public long getLastHeartBeat() {
		return lastHeartBeat;
	}

	/**
	 * @param lastHeartBeat the lastHeartBeat to set
	 */
	public void setLastHeartBeat(long lastHeartBeat) {
		this.lastHeartBeat = lastHeartBeat;
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * 
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * 
	 * @return the server
	 */
	public Server getServer() {
		return server;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
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
		User other = (User) obj;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}

	/**
	 * toString
	 */
	@Override
	public String toString() {
		if (socket == null) {
			return "Client: no socket connection";
		}
		return "Client: " + socket.getRemoteSocketAddress() + "\tid: " + userID;
	}
	
	public void setTeamID(int id) {
		teamID = id;
	}
	
	public int getTeamID() {
		return teamID;
	}

}
