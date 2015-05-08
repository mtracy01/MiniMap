package map.minimap.games.friendFinder;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import map.minimap.MainMenu;
import map.minimap.frameworks.coreResources.IDCipher;
import map.minimap.frameworks.gameResources.Beacon;
import map.minimap.frameworks.gameResources.Game;
import map.minimap.frameworks.gameResources.User;
import map.minimap.frameworks.mapResources.LatLngInterpolator;
import map.minimap.helperClasses.Data;

public class FriendFinderGame extends Game {

	private String LOG_TAG = "FriendFinderGame";

	public FriendFinderGame() {
        beaconsEnabled = true;
	}

	@Override
	public void handleMessage(String message) {

		Log.v("Friend Finder Game", message);
		String[] parts = message.split(" ");
		if (parts[0].equals("location")) {
            User u = findUserbyId(IDCipher.unCipher(parts[1]), Data.players);
            if (u == null) {
            	return;
            }
            LatLng ll = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            Log.v("userid", parts[1]);
            if (u == null) {
            	Log.e("userid", "is null");
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
							//Log.e(LOG_TAG,"User marker did not exist! Creating one in FriendFinderGame...");
							//u.setMarker(Data.map.addMarker(new MarkerOptions().position(u.getCoordinates())));
						}
					}
            	}
            });

		} else if (parts[0].equals("addbeacon")) {
            int id = Integer.parseInt(parts[1]);
            LatLng loc = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            addBeacon(id, loc);
		} else if (parts[0].equals("removebeacon")) {
            removeBeacon(Integer.parseInt(parts[1]));
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
                final User removedUser = findUserbyId(IDCipher.unCipher(parts[1]), Data.players);
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
	public void addBeacon(int beaconID, LatLng loc) {
		// Add a beacon
        Data.user.addBeacon(new Beacon(loc, beaconID, Data.user.getTeam()));
	}

	/**
	 * teamID should always be 0
	 */
	@Override
	public void removeBeacon(Integer id) {
        Log.v("Friend Finder Game", "Removing beacon with id: " + id);
        Data.user.removeBeaconByID(id);
	}
}
