package map.minimap.frameworks;

/**
 * Created by Corey on 2/17/2015.
 */

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import map.minimap.FriendFinder;
import map.minimap.LoginActivity;
import map.minimap.games.AssassinsGame;
import map.minimap.games.FriendFinderGame;
import map.minimap.games.SardinesGame;
import map.minimap.helperClasses.Data;
import map.minimap.mainActivityComponents.LobbyFragment;

import android.widget.Toast;

import com.facebook.login.LoginManager;


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

    private String newGameType;

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
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Log.e("ServerConnection", "Could not connect to server");

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

                // Remove all current users
                if (Data.users == null) {
                    Data.users = new ArrayList<User>();
                } else {
                    Data.users.clear();
                }
                for(int i =1; i < parts.length; i++){
                    // Add the existing user if it is us, otherwise create a new one
                    if (Data.user.getID().equals(parts[i])) {
                        Data.users.add(Data.user);
                    } else {
                        Data.users.add(new User(parts[i]));
                   }
                }
                LobbyFragment.changeGrid();
            } else if(parts[0].equals("game")){
                Log.v("gameId", parts[1]);
                Data.gameId = parts[1];
                switch(newGameType) {
                    case "friendFinder":
                        Data.user.setGame(new FriendFinderGame());
                        break;
                    case "assassins":
                        Data.user.setGame(new AssassinsGame());
                        break;
                    case "marcoPolo":
                        Data.user.setGame(new SardinesGame());
                        break;
                }

                Data.user.setInGame(true);
            } else if (parts[0].equals("invite")) {
                Log.v("invite", parts[2]);
                // We got an invite, lets join (temporary, normally should ask)
                // TODO handle the invite correctly, parts[1] contains the type, parts[2] contains the id
                acceptGameMessage(parts[2]);
                Data.gameId = parts[2];
                Data.user.setGame(new FriendFinderGame());
                Data.user.setInGame(true);
            } else if (parts[0].equals("users")) {
                Data.users = new ArrayList<>();
                for(int i =1; i < parts.length;i++){
                    Data.users.add(new User(parts[i]));
                }
                for(User u : Data.users){
                    Data.client.sendMessage("invite " + Data.gameId +" "+u.getID());
                }
                LobbyFragment.changeGrid();
            } else if(parts[0].equals("gameStart")) {
                Maps.setCenterPosition(Data.user);
                // TODO: Don't make this only start friend finder...
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
        if (connected) {
            out.println(message);
        }
    }
    public void createGameMessage(String gameType){
        newGameType = gameType;
        if (connected) {
            out.println("createGame " + gameType);
        }
    }
    public void acceptGameMessage(String gameID){
        if (connected) {
            out.println("accept " + gameID);
        }
    }
    public void rejectGameMessage(String gameID){
        if (connected) {
            out.println("reject " + gameID);
        }
    }
    public void getAllUsers(){
        if (connected) {
            out.println("getAllUsers");
        }
    }
    public void startGame() {
        if (connected) {
            out.println("start " + Data.gameId);
        }
    }


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

        // Put any code we want to call if the connection fails here
        Toast toast = Toast.makeText(Data.mainAct.getApplicationContext(), "Disconnected from server", Toast.LENGTH_SHORT);
        toast.show();

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