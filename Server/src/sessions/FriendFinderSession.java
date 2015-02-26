package sessions;

import java.util.ArrayList;
import server.Team;
import server.Location;
import server.User;
import server.Beacon;

public class FriendFinderSession extends GameSession {

	public FriendFinderSession(ArrayList<User> users) {
		super(users, "friendFinder");
		teams.add(new Team(0)); //There is only one team in a friend finder session
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

	}

	@Override
	/* mid-game */
	public void addUser(User user) {
		// TODO Auto-generated method stub
		user.setTeamID(0);
		getTeambyID(teams, 0).addUser(user);

	}

	@Override
	public void addBeacon(Location loc) {
		// TODO Auto-generated method stub
		Beacon beacon = new Beacon(loc);
		beacon.setTeamId(0);
		getTeambyID(teams, 0).addBeacon(beacon);

	}

	@Override
	public void removeBeacon(int teamid, Integer id) {
		// TODO Auto-generated method stub
		getTeambyID(teams, teamid).removeBeacon((getTeambyID(teams, teamid).getBeaconbyID(id)));
	}

}
