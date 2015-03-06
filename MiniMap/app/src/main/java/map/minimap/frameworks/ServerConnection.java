package map.minimap.frameworks;

/**
 * Created by Corey on 2/17/2015.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import map.minimap.FriendFinder;
import map.minimap.helperClasses.Data;
import map.minimap.mainActivityComponents.LobbyFragment;
import map.minimap.games.*;


public class ServerConnection extends Thread {

    public static final int SERVER_PORT = 2048;
    public static final String SERVER_IP = "tracy94.com";

    //private map.minimap.MainActivity activity;
    private Socket socket;
    private String user_ID;
    // Input/output
    private PrintWriter out;
    private Scanner in;

    // Are we connected to the client?
    private boolean connected;

    public ServerConnection( String ID) {
      //  this.activity =  activity;

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
            Log.v("Message", message);
            String[] parts = message.split(" ");

            if(parts[0].equals("gameUsers")){
                LobbyFragment.playersList = new ArrayList<String>();
                LobbyFragment.playersList.add(Data.user.getName());
                // Remove all current users
                Data.users.clear();
                for(int i =1; i < parts.length; i++){
                    LobbyFragment.playersList.add(parts[i]);

                    // Maintain an up to date list of users
                    Data.users.add(new User(parts[i]));
                }
                LobbyFragment.changeGrid();
            } else if(parts[0].equals("game")){
                Log.v("gameId", parts[1]);
                Data.gameId = parts[1];
                Data.user.setGame(new FriendFinderGame());
                Data.user.setInGame(true);
            } else if (parts[0].equals("invite")) {
                Log.v("invite", parts[1]);
                // We got an invite, lets join (temporary, normally should ask)
                acceptGameMessage(parts[1]);
                Data.gameId = parts[1];
                Data.user.setGame(new FriendFinderGame());
                Data.user.setInGame(true);
            } else if (parts[0].equals("users")) {
                Data.users = new ArrayList<User>();
                for(int i =1; i < parts.length;i++){
                    Data.users.add(new User(parts[i]));
                }
                LobbyFragment.playersList = new ArrayList<String>();
                for(User u : Data.users){
                    LobbyFragment.playersList.add(u.getName());
                    Data.client.sendMessage("invite " + Data.gameId +" "+u.getID());
                }
                LobbyFragment.changeGrid();
            }else if(parts[0].equals("location")) {
                if(Data.users !=null) {
                    for (int i = 0; i < Data.users.size(); i++) {
                        if (Data.users.get(i).getID().equals(parts[1])) {
                            Data.users.get(i).getMarker().setPosition(new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
                        }
                    }
                }

            }
            else if(parts[0].equals("gameStart")) {
                Intent intent = new Intent(Data.mainAct.getApplicationContext(), FriendFinder.class);
                Data.mainAct.startActivity(intent);
                Data.user.getGame().startSession();
            }
            else if (Data.user.getInGame()) {
                Data.user.getGame().handleMessage(message);
            }

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
    public void createGameMessage(String gameType){
        out.println("createGame " + gameType);
    }
    public void acceptGameMessage(String gameID){
        out.println("accept "+ gameID);
    }
    public void rejectGameMessage(String gameID){
        out.println("reject "+ gameID);
    }
    public void getAllUsers(){
        out.println("getAllUsers");
    }
    public void startGame() {out.println("start " + Data.gameId);}


    public boolean isConnected() {
        return connected;
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
        return "Client: " + user_ID + " "+ socket.getRemoteSocketAddress();
    }

}