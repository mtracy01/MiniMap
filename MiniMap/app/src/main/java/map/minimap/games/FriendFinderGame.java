package map.minimap.games;


import android.content.Intent;
import android.util.Log;

import map.minimap.FriendFinder;
import map.minimap.frameworks.*;
import com.google.android.gms.maps.model.LatLng;

public class FriendFinderGame extends Game {

	//private static final Logger log = Logger.getLogger( Server.class.getName() );
	

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

		} else if (parts[0].equals("addbeacon")) {

		} else if (parts[0].equals("removebeacon")) {

		}
		
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
