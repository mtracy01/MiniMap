package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

import sessions.GameSession;


public class Server extends Thread {
	
	/**
	 * The port the server runs on
	 */
	public static final int SERVER_PORT = 2048;
	
	/**
	 * A list of connected clients
	 */
	private ArrayList<User> connectedUsers;
	
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
	private ArrayList<GameSession> gameSessions;
	
	/**
	 * Create the list of connected users
	 */
	public Server() {
		connectedUsers = new ArrayList<User>();
		gameSessions = new ArrayList<GameSession>();
	}
	
	@Override
	public void run() {
		startServer();
	}
	
	/**
	 * Start accepting incoming connections
	 */
	public void startServer() {
		System.out.println("Server starting...");
		
		// Open the server socket
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			System.out.println("Socket creation failed.");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server running...");
		
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
		System.out.println("Server stopping...");
		listening = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	/**
	 * Add a user
	 * @param u
	 */
	public void addUser(User u) {
		synchronized (connectedUsers) {
			connectedUsers.add(u);
			System.out.println(connectedUsers.size() + " connected clients.");
		}
	}
	
	/**
	 * Remove a user
	 * @param c
	 */
	public void removeUser(User u) {
		synchronized (connectedUsers) {
			connectedUsers.remove(u);
			System.out.println(connectedUsers.size() + " connected clients.");
		}
	}
	
	/**
	 * Add a game session
	 * @param session
	 */
	public void addSession(GameSession session) {
		synchronized (gameSessions) {
			gameSessions.add(session);
			System.out.println(gameSessions.size() + " running sessions.");
		}
	}
	
	/**
	 * Remove a game session
	 * @param session
	 */
	public void removeSession(GameSession session) {
		session.endSession();
		synchronized (gameSessions) {
			gameSessions.remove(session);
			System.out.println(gameSessions.size() + " running sessions.");
		}
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
