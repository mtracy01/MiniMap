package sessions;

import java.util.ArrayList;
import server.Team;
import server.Location;
import server.User;
import server.Beacon;

public class FriendFinderSession extends GameSession {

	public FriendFinderSession(User owner) {
		super("friendFinder", owner);
		teams.add(new Team()); //There is only one team in a friend finder session
		// TODO: Add the owner to a team
	}

	@Override
	public void handleMessage(String message, User user) {
		// TODO Auto-generated method stub
		
		

	}
	
	@Override
	/* called when user presses start button
	 * assign teams, etc...
	 *  */
	public void startSession() {
		// TODO Auto-generated method stub
		
	}

	@Override
	/*
	 * everyone is gone? 
	 * @see sessions.GameSession#endSession()
	 */
	public void endSession() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(User user) {
		// TODO Auto-generated method stub
		getTeambyID(teams, user.getTeamID()).removeUser(user);
		synchronized (users) {
			users.remove(user);
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
