package map.minimap.games.sardines;

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
import map.minimap.frameworks.gameResources.Beacon;
import map.minimap.frameworks.gameResources.Game;
import map.minimap.frameworks.mapResources.Maps;
import map.minimap.frameworks.gameResources.User;
import map.minimap.helperClasses.Data;
//TODO: Specifics of Sardines
public class SardinesGame extends Game {

	public SardinesGame() {}

	@Override
	public void handleMessage(String message) {
        Log.v("Sardines Game", message);
        String[] parts = message.split(" ");
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

        } else if (parts[0].equals("Found")) {
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
                            Data.client.sendMessage("Found " + targetUser.getID() + " true");
                        }
                    });
                    builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("Found " + targetUser.getID() + " false");
                        }
                    });
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
			
        } else if (parts[0].equals("TeamChange")) {
            //erase everyone from the map.
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    if (Data.map == null) {
                        return;
                    }
                    for (User u : Data.players) {
                        if (u.getMarker() != null) {
                            u.getMarker().remove();
                        }
                    }
                    //remove all beacons from this user
                    Data.map.clear();
                }
            });
            User userMoved = findUserbyId(parts[1], Data.players);
            if (userMoved != null) {
                userMoved.setTeam(Integer.parseInt(parts[2]));
            }
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
		Log.v("Friend Finder Game", "Starting game session " + this.getId());
		isRunning = true;
        Data.gameStarted = true;
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
		Log.v("Friend Finder Game", "Removing user from friendfinder session");
	}

	@Override
	/* mid-game */
	public void addUser(User user, int teamid) {}

	/**
	 * teamID should always be 0 in FriendFinder
	 */
	
	@Override
	public void addBeacon(int teamid, LatLng loc) {}

	/**
	 * teamID should always be 0
	 */
	@Override
	public void removeBeacon(Integer id) {}
	
	
	
	
	
}
