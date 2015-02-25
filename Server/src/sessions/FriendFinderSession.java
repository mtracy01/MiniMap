package sessions;

import java.util.ArrayList;

import server.Location;
import server.User;

public class FriendFinderSession extends GameSession {

	public FriendFinderSession(ArrayList<User> users) {
		super(users, "friendFinder");
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

	}

	@Override
	public void addBeacon(Location loc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeBeacon(Integer id) {
		// TODO Auto-generated method stub
		
	}

}
