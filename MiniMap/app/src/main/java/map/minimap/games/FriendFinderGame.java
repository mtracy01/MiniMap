package map.minimap.games;

import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import map.minimap.frameworks.*;
import map.minimap.frameworks.MapResources.LatLngInterpolator;
import map.minimap.helperClasses.Data;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class FriendFinderGame extends Game {

	//private static final Logger log = Logger.getLogger( Server.class.getName() );
	private String LOG_TAG = "FriendFinderGame";

	public FriendFinderGame() {

	}


	@Override
	public void processLogic() {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void handleMessage(String message) {
		// TODO Auto-generated method stub
		Log.v("Friend Finder Game", message);
		String[] parts = message.split(" ");
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

					LatLngInterpolator mLatLngInterpolator;
					for(User u : Data.players) {
						if(u.getMarker() != null){
							mLatLngInterpolator = new LatLngInterpolator.Linear();
							Log.i(LOG_TAG, "User: " + u.getName() + "Animating to: " + u.getCoordinates().toString());
							Data.mapFragment.animateMarkerToGB(u.getMarker(), u.getCoordinates(), mLatLngInterpolator, 1500);
							if(u.getID().equals(Data.user.getID()))
								//Data.map.moveCamera(CameraUpdateFactory.newLatLngZoom(Data.user.getCoordinates(), 15));
								Data.map.animateCamera(CameraUpdateFactory.newLatLng(Data.user.getCoordinates()), 1500, new GoogleMap.CancelableCallback() {
									@Override
									public void onFinish() {

									}

									@Override
									public void onCancel() {

									}
								});
						}
						else{
							//Note: this is safety code in case a user marker does not exist. **This should never be run!!!**
							Log.e(LOG_TAG,"User marker did not exist! Creating one in FriendFinderGame...");
							u.setMarker(Data.map.addMarker(new MarkerOptions().position(u.getCoordinates())));
						}
					}
            	}
            });

		} else if (parts[0].equals("addbeacon")) {

		} else if (parts[0].equals("removebeacon")) {

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
