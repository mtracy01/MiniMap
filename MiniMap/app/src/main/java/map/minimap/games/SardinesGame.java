package map.minimap.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import map.minimap.frameworks.Game;
import map.minimap.frameworks.MapResources.Maps;
import map.minimap.frameworks.User;
import map.minimap.helperClasses.Data;
import map.minimap.frameworks.Beacon;
//TODO: Specifics of Sardines
public class SardinesGame extends Game {

	//private static final Logger log = Logger.getLogger( Server.class.getName() );


	public SardinesGame() {

	}


	@Override
	public void processLogic() {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void handleMessage(String message) {
        // TODO Auto-generated method stub
        Log.v("Sardines Game", message); //I just changed this to Sardines...
        String[] parts = message.split(" ");
        if (parts[0].equals("location")) {
            User user = findUserbyId(parts[1], Data.players);
            if (user == null) {
                return;
            }
            LatLng ll = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            if (user.getTeam() == -1)
            {
                user.setTeam(Integer.parseInt(parts[4]));
            }

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
            Beacon beac = new Beacon(b, Integer.parseInt(parts[1]));
            Data.user.addBeacon(beac);

            Data.map.setMyLocationEnabled(true);
            Data.map.moveCamera(CameraUpdateFactory.newLatLngZoom(b, 13));

            Data.map.addMarker(new MarkerOptions()
                    .title("Beacon")
                    .snippet("Beacon set by Player")
                    .position(b)
                    );

        } else if (parts[0].equals("removebeacon")) {
            Data.user.removeBeaconByID((Integer.parseInt(parts[1])));

        } else if (parts[0].equals("Found")) {
            //make a dialogue to ask if ___ has found this user
            final String id = parts[1];
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    User targetUser = findUserbyId(id, Data.players);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Data.gameActivity);
                    // Add the buttons
                    builder.setMessage("Has " + targetUser.getName() + " found you?");
                    //builder.setMessage("Confirm Kill?");
                    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("Found " + id + " false");
                        }
                    });
                    builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("Found " + id + " true");
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
            if (Data.user.getID().equals(parts[1])); //If this is the user that is changing teams...
                Data.user.setTeam(Integer.parseInt(parts[2])); //change the teamID
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
