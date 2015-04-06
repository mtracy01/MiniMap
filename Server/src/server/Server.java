package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import sessions.GameSession;


public class Server extends Thread {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	/**
	 * The port the server runs on
	 */
	public static final int SERVER_PORT = 2048;
	
	/**
	 * Timeout is 11 seconds
	 */
	public static final long TIMEOUT = 11000;
	
	/**
	 * A list of connected clients
	 */
	private Set<User> connectedUsers;
	
	/**
	 * The ServerSocket
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Is the server accepting connections?
	 */
	private boolean listening = false;
	
	/**
	 * A list of currently running game sessions.
	 */
	private Set<GameSession> gameSessions;
	
	/**
	 * Create the list of connected users
	 */
	public Server() {
		connectedUsers = new HashSet<User>();
		gameSessions = new HashSet<GameSession>();
		
		// Set up logging
		Logger l = Logger.getLogger("");
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		l.addHandler(handler);
		l.setLevel(Level.ALL);
	}

	
	/**
	 * Send a list of all currently connected users to user
	 * @param user
	 */
	public void sendAllUsers(User user) {
		StringBuilder usersMessage = new StringBuilder();
		usersMessage.append("users");
		synchronized (connectedUsers) {
			for (User u : connectedUsers) {
				usersMessage.append(' ');
				usersMessage.append(u.getUserID());
			}
		}
		user.sendMessage(usersMessage.toString());
	}
	
	
	/*
	 * --------------------- Add/Remove Users and Sessions ---------------------
	 */
	
	/**
	 * Add a user
	 * @param u
	 */
	public boolean addUser(User u) {
		synchronized (connectedUsers) {
			// If the heartbeat is past the timeout, remove the old user
			if (connectedUsers.contains(u)) {
				User previous = null;
				for (User user : connectedUsers) {
					if (user.equals(u)) {
						previous = user;
						break;
					}
				}
				if (previous != null) {
					long currentTime = System.currentTimeMillis();
					if (currentTime - previous.getLastHeartBeat() > TIMEOUT) {
						log.fine("Removing preexisting user");
						previous.closeSocket();
						connectedUsers.remove(previous);
					}
				}
			}
			boolean success = connectedUsers.add(u);
			log.fine(connectedUsers.size() + " connected clients.");
			return success;
		}
	}
	
	/**
	 * Remove a user
	 * @param c
	 */
	public void removeUser(User u) {
		synchronized (connectedUsers) {
			log.fine("Removing user: " + u);
			connectedUsers.remove(u);
			log.fine(connectedUsers.size() + " connected clients.");
		}
	}
	
	/**
	 * Get the user by their id.  If the specified user does not exist, return null.
	 * @param id
	 * @return
	 */
	public User getUserByID(String id) {
		synchronized (connectedUsers) {
			for (User u : connectedUsers) {
				if (u.getUserID().equals(id)) {
					return u;
				}
			}
		}
		return null;
	}
	
	/**
	 * Add a game session
	 * @param session
	 */
	public void addSession(GameSession session) {
		synchronized (gameSessions) {
			gameSessions.add(session);
			log.log(Level.FINER, "Adding game session of type {0}", session.getClass().getName());
			log.finer(gameSessions.size() + " running sessions.");
		}
	}
	
	/**
	 * Remove a game session
	 * @param session
	 */
	public void removeSession(GameSession session) {
		if (session.isRunning()) {
			session.endSession();
		}
		synchronized (gameSessions) {
			gameSessions.remove(session);
			log.finer("Removing session");
			log.finer(gameSessions.size() + " running sessions.");
		}
	}

	public GameSession getSessionByID(int id) {
		synchronized (gameSessions) {
			for (GameSession session : gameSessions) {
				if (session.getId() == id) {
					return session;
				}
			}
		}
		return null;
	}
	
	
	/*
	 * ---------------------------- Server Operation ----------------------------
	 */

	@Override
	public void run() {
		startServer();
	}
	
	/**
	 * Start accepting incoming connections
	 */
	public void startServer() {
		log.info("Server starting...");
		
		// Open the server socket
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			System.out.println("Socket creation failed.");
			e.printStackTrace();
			System.exit(1);
		}
		log.info("Server running...");
		
		listening = true;
		while (listening) {
			User client;
			try {
				client = new User(serverSocket.accept(), this);
				client.start();
			} catch (IOException e) {
				// This check is to prevent printing an exception when the server shuts down
				if (listening) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Stop the server and disconnect any connected users
	 */
	public void stopServer() {
		log.info("Server stopping...");
		listening = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	/**
	 * Print the status of the server
	 */
	public void printStatus() {
		synchronized (connectedUsers) {
			for (User u : connectedUsers) {
				System.out.println(u);
			}
			System.out.println(connectedUsers.size() + " connected users.");
		}
		synchronized (gameSessions) {
			for (GameSession session : gameSessions) {
				System.out.println(session);
			}
			System.out.println(gameSessions.size() + " running game sessions");
		}
	}
	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
		Scanner input = new Scanner(System.in);
		/*
		 * Process server commands
		 */
		while (input.hasNextLine()) {
			String line = input.nextLine();
			if (line.equalsIgnoreCase("stop")) {
				server.stopServer();
			} else if (line.equalsIgnoreCase("status")) {
				server.printStatus();
			}
		}
	}
}
