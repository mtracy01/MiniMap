package map.minimap.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import map.minimap.frameworks.Game;
import map.minimap.frameworks.MapResources.LatLngInterpolator;
import map.minimap.frameworks.MapResources.Maps;
import map.minimap.frameworks.User;
import map.minimap.helperClasses.Data;
//TODO: Specifics of Sardines
public class AssassinsGame extends Game {

    private String LOG_TAG = "AssasinsGame";
    //private static final Logger log = Logger.getLogger( Server.class.getName() );
    private User target;

    public AssassinsGame() {
         target = null;
    }


    @Override
    public void processLogic() {
        // TODO Auto-generated method stub


    }

    @Override
    public void handleMessage(String message) {
        // TODO Auto-generated method stub
        Log.v("Assassins Game", message); //I just changed this to Sardines...
        final String[] parts = message.split(" ");
        if (parts[0].equals("location")) {
            User u = findUserbyId(parts[1], Data.players);
            if (u == null) {
                return;
            }
            LatLng ll = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            Log.v("userid", parts[1]);
            if (u == null) {
                Log.v("userid", "is null");
            }
            u.setCoordinates(ll);

            // We can only update locations from the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    if (Data.map == null) {
                        return;
                    }
                    ArrayList<User> players = new ArrayList<User>();
                    players.add(Data.user);
                    if (target != null) {
                        players.add(target);
                    }

                    LatLngInterpolator mLatLngInterpolator;
                    for(User u : players) {
                        if (u.getMarker() != null) {
                            mLatLngInterpolator = new LatLngInterpolator.Linear();
                            Data.mapFragment.animateMarkerToGB(u.getMarker(), u.getCoordinates(), mLatLngInterpolator, 1500);
                        } else {
                            //Note: this is safety code in case a user marker does not exist. **This should never be run!!!**
                            Log.e(LOG_TAG, "User marker did not exist! Creating one in FriendFinderGame...");
                            u.setMarker(Data.map.addMarker(new MarkerOptions().position(u.getCoordinates())));
                        }
                    }
                }
            });

        } else if (parts[0].equals("addbeacon")) {

        } else if (parts[0].equals("removebeacon")) {

        } else if (parts[0].equals("target")) {
            User u = findUserbyId(parts[1], Data.players);
            if (u == null) {
                return;
            }
            target = u;

        } else if (parts[0].equals("acceptDeath")) {
            Log.v("Assassins Game", "acceptDeath received");
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    Log.v("Assassins Game", "Running on ui thread");
                    User assassin = findUserbyId(parts[1], Data.players);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Data.gameActivity);
                    // Add the buttons
                    builder.setMessage("Confirm death from " + assassin.getName() + "?");
                    //builder.setMessage("Confirm Death?");
                    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmDeath true");
                        }
                    });
                    builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmDeath false");
                        }
                    });

                    Log.v("Assassins Game", "builder created");
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Log.v("Assassins Game", "dialog show");
                }
            });


        } else if (parts[0].equals("acceptKill")) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    Log.v("Assassins Game", "Running on ui thread");
                    User targetUser = findUserbyId(parts[1], Data.players);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Data.gameActivity);
                    // Add the buttons
                    builder.setMessage("Confirm kill of " + targetUser.getName() + "?");
                    //builder.setMessage("Confirm Kill?");
                    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmKill true");
                        }
                    });
                    builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmKill false");
                        }
                    });

                    Log.v("Assassins Game", "builder created");
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Log.v("Assassins Game", "dialog show");
                }
            });


        } else if (parts[0].equals("kill")) {

        } else {

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
        Log.v("Assassins Game", "Starting game session " + this.getId());
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
        Log.v("Friend Finder Game", "Removing user from friendfinder session");
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
    public void removeBeacon(int teamid, Integer id) {
        // TODO Auto-generated method stub
        getTeambyID(teams, teamid).removeBeacon((getTeambyID(teams, teamid).getBeaconbyID(id)));
    }





}
