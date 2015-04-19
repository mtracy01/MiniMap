package map.minimap.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import map.minimap.MainMenu;
import map.minimap.frameworks.Beacon;
import map.minimap.frameworks.Game;
import map.minimap.frameworks.MapResources.Maps;
import map.minimap.frameworks.User;
import map.minimap.helperClasses.Data;

/**
 * Created by michael on 4/19/2015.
 */
public class CaptureTheFlagGame extends Game {

    public CaptureTheFlagGame() {
        beaconsEnabled = true;
    }


    @Override
    public void processLogic() {
        // TODO Auto-generated method stub


    }

    @Override
    public void handleMessage(String message) {
        // TODO Auto-generated method stub
        Log.v("CTF Game", message); //I just changed this to Sardines...
        final String[] parts = message.split(" ");
        if (parts[0].equals("location")) {
            User user = findUserbyId(parts[1], Data.players);
            if (user == null) {
                return;
            }
            LatLng ll = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            user.setTeam(Integer.parseInt(parts[4]));

            Log.v("userid", parts[1]);
            if (user == null) {
                Log.v("userid", "is null");
            }
            user.setCoordinates(ll);

            // We can only update locations from the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    if (Data.map == null) {
                        return;
                    }
                    ArrayList<User> teammates = new ArrayList<>();
                    for (User u : Data.players) {
                        if (u.getMarker() != null) {
                            u.getMarker().remove();
                        }
                        if (u.getTeam() == Data.user.getTeam())
                        {
                            teammates.add(u);
                        }
                    }
                    Data.map.clear();
                    Maps.initializePlayers(Data.map, teammates);
                }
            });


        } else if (parts[0].equals("addbeacon")) {

            LatLng b = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            Beacon beac = new Beacon(b, Integer.parseInt(parts[1]), Data.user.getTeam());
            Data.user.addBeacon(beac);

        } else if (parts[0].equals("removebeacon")) {
            Data.user.removeBeaconByID((Integer.parseInt(parts[1])));

        } else if (parts[0].equals("acceptTagged")) {
            //make a dialogue to ask if ___ has found this user
            final String id = parts[1];
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    final User targetUser = findUserbyId(id, Data.players);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Data.gameActivity);
                    // Add the buttons
                    builder.setMessage("Has " + targetUser.getName() + " found you?");
                    //builder.setMessage("Confirm Kill?");
                    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmTagged " + targetUser.getID() + " true");
                        }
                    });
                    builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmTagged " + targetUser.getID() + " false");
                        }
                    });
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } else if (parts[0].equals("userRemoved")) {
            if (parts[1].equals(Data.user.getID())) {
                Data.user.setInGame(false);
                Data.user.setGame(null);
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), "You have been removed from the game.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                Data.gameActivity.startActivity(new Intent(Data.gameActivity,MainMenu.class));
            } else {
                final User removedUser = findUserbyId(parts[1], Data.players);
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), removedUser.getName() + " has left the game.", Toast.LENGTH_SHORT);
                        toast.show();
                        removedUser.getMarker().remove();
                        Data.players.remove(removedUser);
                    }
                });
            }
        } else if (parts[0].equals("flagReturned")) {
            if (parts[1].equals(Data.user.getID())) {

                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), "You returned the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                //Data.gameActivity.startActivity(new Intent(Data.gameActivity,MainMenu.class));
            } else {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        String name = findUserbyId(parts[1], Data.players).getName();
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(),  name + " has returned the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        } else if (parts[0].equals("flagCaptured")) {
            if (parts[1].equals(Data.user.getID())) {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), "You captured the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                //Data.gameActivity.startActivity(new Intent(Data.gameActivity,MainMenu.class));
            } else {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        String name = findUserbyId(parts[1], Data.players).getName();
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(),  name + " has captured the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }

    }

    public User findUserbyId(String theid, ArrayList<User> users) {
        for (User u: users) {
            if (u.getID().equals(theid)) {
                return u;
            }
        }
        return null;

    }





    @Override
	/* called when user presses start button
	 * assign teams, etc...
	 *  */
    public void startSession() {
        Log.v("Capture The Flag Game", "Starting game session " + this.getId());
        isRunning = true;
    }

    @Override
	/*
	 * everyone is gone?
	 * @see sessions.GameSession#endSession()
	 */
    public void endSession() {
        isRunning = false;

    }

    @Override
    public void removeUser(User user) {
        Log.v("Capture the Flag Game", "Removing user from ctf session");
    }

    @Override
	/* mid-game */
    public void addUser(User user, int teamid) {
        // TODO Auto-generated method stub
    }

    /**
     * teamID should always be 0 in FriendFinder
     */

    @Override
    public void addBeacon(int teamid, LatLng loc) {
        // TODO Auto-generated method stub
        // Beacon beacon = new Beacon(loc);
        // beacon.setTeamId(teamid);
        // getTeambyID(teams, teamid).addBeacon(beacon);

    }

    /**
     * teamID should always be 0
     */
    @Override
    public void removeBeacon(Integer id) {
        // TODO Auto-generated method stub
    }



}
