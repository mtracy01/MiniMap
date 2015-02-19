package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import sessions.GameSession;


public class User extends Thread {

	private Server server;
	private Socket socket;
	
	// Input/output
	private PrintWriter out;
	private Scanner in;
	
	// Are we connected to the client?
	private boolean connected;
	
	/**
	 * The current game session
	 */
	private GameSession gameSession;
	/**
	 * Are we in a game?
	 */
	private boolean inGame;
	
	public User(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		connected = false;
	}
	
	@Override
	public void run() {
		try {
			// Create a new PrintWriter with auto flush on
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new Scanner(socket.getInputStream());
		} catch (IOException e) {
			// Something went wrong
			System.out.println(e);
			e.printStackTrace();
			// Cannot communicate, try closing socket
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// Return
			return;
		}
		
		// We are connected
		connected = true;
		// We add the client to the server here because we know we can read/write from it
		server.addUser(this);
		
		try {
			String line = null;
			while(in.hasNextLine()) {
				line = in.nextLine();
				handleMessage(line);
			}
		} catch (Exception e) {
			// When the socket is closed, an exception may be thrown
			// We only care about exceptions when we are still connected
			if (connected) {
				System.out.println(e);
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
		System.out.println(socket.getRemoteSocketAddress() + ": " + message);
		try {
			// Handle messages here
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
		out.println(message);
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
			System.out.println(e);
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
	 * toString
	 */
	@Override
	public String toString() {
		if (socket == null) {
			return "Client: null";
		}
		return "Client: " + socket.getRemoteSocketAddress();
	}

}
