package games;

import java.util.logging.Logger;

import android.util.log;
import server.Beacon;
import server.Location;
import server.Server;
import server.Team;
import server.User;

public class FriendFinderGame extends Game {

	//private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	@Override
	public void processLogic() {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void handleMessage() {
		// TODO Auto-generated method stub
		
		
		
	}
	
	/**
	 * Send the game start message to all users
	 */
	protected void sendStartMessage() {
		String message = "gameStart " + id;
		synchronized (users) {
			for (User u : users) {
				u.sendMessage(message);
			}
		}
	}
	
	

	
	@Override
	/* called when user presses start button
	 * assign teams, etc...
	 *  */
	public void startSession() {
		log.v("Starting game session " + this.getId());
		isRunning = true;
		
		//Put all users on the same team for friendfinder
		for (User user: this.users)
		{
			teams.get(0).addUser(user);
		}
		
		
		// Last thing after setting up the game, send the start message
		sendStartMessage();
	}

	@Override
	/*
	 * everyone is gone? 
	 * @see sessions.GameSession#endSession()
	 */
	public void endSession() {
		isRunning = false;
		
		for (User user: this.users)
		{
			removeUser(user);
		}
		for (Team team: this.teams)
		{
			//might be a temporary solution
			team.removeAllBeacons();
		}
		
	}

	@Override
	public void removeUser(User user) {
		Log.v("Removing user from friendfinder session");
		user.setInGame(false);
		user.setGameSession(null);
		if (getTeambyID(teams, user.getTeamID()) != null) {
			getTeambyID(teams, user.getTeamID()).removeUser(user);
		}
		synchronized (users) {
			Log.v(users.toString());
			users.remove(user);
			Log.v(users.toString());
			Log.v(users.size() + " users in session");
			if (users.isEmpty()) {
				endSession();
			}
			if (owner.equals(user) && !users.isEmpty()) {
				owner = users.get(0);
			}
		}
	}

	@Override
	/* mid-game */
	public void addUser(User user, int teamid) {
		// TODO Auto-generated method stub
		user.setTeamID(teamid);
		getTeambyID(teams, teamid).addUser(user);
		synchronized (users) {
			users.add(user);
		}
	}

	/**
	 * teamID should always be 0 in FriendFinder
	 */
	
	@Override
	public void addBeacon(int teamid, Location loc) { 
		// TODO Auto-generated method stub
		Beacon beacon = new Beacon(loc);
		beacon.setTeamId(teamid);
		getTeambyID(teams, teamid).addBeacon(beacon);

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
