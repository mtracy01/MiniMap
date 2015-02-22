package map.minimap.frameworks;

/**
 * Created by Corey on 2/17/2015.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import android.app.Activity;
import android.util.Log;


public class ServerConnection extends Thread {

    public static final int SERVER_PORT = 2048;
    public static final String SERVER_IP = "tracy94.com";

    private map.minimap.MainActivity activity;
    private Socket socket;
    private String user_ID;
    // Input/output
    private PrintWriter out;
    private Scanner in;

    // Are we connected to the client?
    private boolean connected;

    public ServerConnection(map.minimap.MainActivity activity, String ID) {
        this.activity =  activity;
        connected = false;
        user_ID = ID;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            // Create a new PrintWriter with auto flush on
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            out.println("id "+ user_ID);
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

        try {
            String[] parts = message.split(" ");

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
    public void createGameMessage(String gameType, User[] players){
        out.print("create Game");
        for(User u: players){
            out.print(" " +u.getID());
        }
        out.println();
    }
    public void acceptGameMessage(String gameID){
        out.println("accept "+ gameID);
    }
    public void rejectGameMessage(String gameID){
        out.println("reject "+ gameID);
    }
    public void getAllUsers(){
        out.println("request users");
    }

    /**
     * Close the socket and any input/output streams
     */
    public void closeSocket() {
        // Prevent double closings
        if (!connected) {
            return;
        }
        // We are no longer connected
        connected = false;

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