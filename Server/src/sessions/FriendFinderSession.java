package sessions;

import java.util.ArrayList;

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
	public void startSession() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endSession() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUser(User user) {
		// TODO Auto-generated method stub

	}

}
