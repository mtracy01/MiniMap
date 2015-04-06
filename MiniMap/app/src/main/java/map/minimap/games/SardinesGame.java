package map.minimap.games;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import map.minimap.frameworks.Game;
import map.minimap.frameworks.Maps;
import map.minimap.frameworks.User;
import map.minimap.helperClasses.Data;
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
            User u = findUserbyId(parts[1], Data.users);
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
                    for (User u : Data.users) {
                        if (u.getMarker() != null) {
                            u.getMarker().remove();
                        }
                    }
                    Data.map.clear();
                    Maps.initializePlayers(Data.map, Data.users);
                }
            });

        } else if (parts[0].equals("addbeacon")) {
            /**
            LatLng sydney = new LatLng(-33.867, 151.206);

            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

            map.addMarker(new MarkerOptions()
                    .title("Sydney")
                    .snippet("The most populous city in Australia.")
                    .position(sydney));
            */
        } else if (parts[0].equals("removebeacon")) {

        } else if (parts[0].equals("Found")) {
            //make a dialogue to ask if ___ has found this user
        } else if (parts[0].equals("TeamChange")) {
            //erase everyone from the map.
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    if (Data.map == null) {
                        return;
                    }
                    for (User u : Data.users) {
                        if (u.getMarker() != null) {
                            u.getMarker().remove();
                        }
                    }
                    Data.map.clear();
                    //Maps.initializePlayers(Data.map, Data.users);
                }
            });
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
