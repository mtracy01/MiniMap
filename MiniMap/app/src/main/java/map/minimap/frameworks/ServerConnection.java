package map.minimap.frameworks;

/**
 * Created by Corey on 2/17/2015.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import map.minimap.FriendFinder;
import map.minimap.frameworks.MapResources.Maps;
import map.minimap.games.Assassins;
import map.minimap.games.AssassinsGame;
import map.minimap.games.FriendFinderGame;
import map.minimap.games.Sardines;
import map.minimap.games.SardinesGame;
import map.minimap.helperClasses.Data;
import map.minimap.mainActivityComponents.LobbyFragment;


public class ServerConnection extends Thread {

    private String LOG_TAG = "ServerConnection";

    public static final int SERVER_PORT = 2048;
    public static final String SERVER_IP = "tracy94.com";

    private Socket socket;
    private String user_ID;
    // Input/output
    private PrintWriter out;
    private Scanner in;

    // Are we connected to the client?
    private boolean connected;

    private String newGameType;

    public ServerConnection( String ID) {

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
            closeSocket();
            // Return
            return;
        }

        // We are connected
        connected = true;

        startHeartBeat();

        try {
            String line;
            while(in.hasNextLine()) {
                line = in.nextLine();
                // Wrapped in try catch to prevent crashes of the server thread
                try {
                    handleMessage(line);
                } catch (Exception e) {
                    Log.e("Server Connection", "Error handling message: " + e.getMessage());
                    e.printStackTrace();
                }
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

            if (parts[0].equals("gameUsers")) {

                // Remove all players from previous game if list is not empty
                Data.players.clear();

                for (int i = 1; i < parts.length; i++) {
                    // Add the existing user if it is us, otherwise create a new one
                    if (Data.user.getID().equals(parts[i])) {
                        Data.players.add(Data.user);
                    } else {
                        User user = Data.user.findUserById(parts[i]);
                        if (user == null) {
                            user = new User(parts[i]);
                            //TODO: we need to add name here, but don't have the name from server yet***
                            try {
                                Thread.sleep(200);
                            } catch (Exception e) {
                                Log.e(LOG_TAG, "Exception on sleep thread for null user handling!");
                            }
                        }
                        Data.players.add(user);
                    }
                }
                LobbyFragment.changeGrid();
            } else if (parts[0].equals("game")) {
                Log.v("gameId", parts[1]);
                Data.gameId = parts[1];
                switch (newGameType) {
                    case "friendFinder":
                        Data.user.setGame(new FriendFinderGame());
                        break;
                    case "assassins":
                        Data.user.setGame(new AssassinsGame());
                        break;
                    case "sardines":
                        Data.user.setGame(new SardinesGame());
                        break;
                }

                Data.user.setInGame(true);
            } else if (parts[0].equals("invite")) {
                Log.v("invite", parts[2]);
                // We got an invite, lets join (temporary, normally should ask)
                // TODO handle the invite correctly, parts[1] contains the type, parts[2] contains the id
                processInvite(parts[1], parts[2]);

            } else if (parts[0].equals("users")) {
                for (int i = 1; i < parts.length; i++) {
                    //If the user is in our friends list, add them to the invitable users list
                    User user = Data.user.findUserById(parts[i]);
                    if (user != null && user.getID() != Data.user.getID())
                        Data.invitableUsers.add(user);
                }

                LobbyFragment.changeGrid();
                Data.clientDoneFlag = 1;
            } else if (parts[0].equals("gameStart")) {
                Maps.setCenterPosition(Data.user);
                // TODO: Don't make this only start friend finder...
                Intent intent = null;
                switch (newGameType) {
                    case "friendFinder":
                        intent = new Intent(Data.mainAct.getApplicationContext(), FriendFinder.class);
                        break;
                    case "assassins":
                        intent = new Intent(Data.mainAct.getApplicationContext(), Assassins.class);
                        break;
                    case "sardines":
                        intent = new Intent(Data.mainAct.getApplicationContext(), Sardines.class);
                        break;
                }
                if (intent != null) {
                    Data.mainAct.startActivity(intent);
                    Data.user.getGame().startSession();
                }
            } else if (Data.user.getInGame()) {
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


    private void processInvite(final String gameType, final String gameID) {
        Data.mainAct.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Data.mainAct);
                // Add the buttons
                String niceType = "";
                switch (gameType) {
                    case "friendFinder":
                        niceType = "Friend Finder";
                        break;
                    case "assassins":
                        niceType = "Assassins";
                        break;
                    case "sardines":
                        niceType = "Sardines";
                        break;
                }
                builder.setMessage("You have been invited to a " + niceType + " game.");
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newGameType = gameType;
                        acceptGameMessage(gameID);
                        Data.gameId = gameID;
                        switch(newGameType) {
                            case "friendFinder":
                                Data.user.setGame(new FriendFinderGame());
                                break;
                            case "assassins":
                                Data.user.setGame(new AssassinsGame());
                                break;
                            case "sardines":
                                Data.user.setGame(new SardinesGame());
                                break;
                        }
                        Data.user.setInGame(true);
                    }
                });
                builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rejectGameMessage(gameID);
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public boolean isConnected() {
        return connected;
    }

    /**
     * Start sending the heartbeat message every 5 seconds
     */
    private void startHeartBeat() {
        new HeartBeatThread(this).start();
    }

    class HeartBeatThread extends Thread {
        public static final int HEARTBEAT_INTERVAL = 5000;
        private ServerConnection connection;

        public HeartBeatThread(ServerConnection connection) {
            this.connection = connection;
        }

        public void run() {
            while (connection.isConnected()) {
                connection.sendMessage("heartbeat");
                try {
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (Exception e) {
                    // Don't do anything
                }
            }
        }
    }

    /**
     * Close the socket and any input/output streams
     */
    public void closeSocket() {
        // Put any code we want to call if the connection fails here
        Data.mainAct.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(Data.mainAct.getApplicationContext(), "Disconnected from server", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

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