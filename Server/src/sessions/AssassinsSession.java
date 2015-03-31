package sessions;

import server.Location;
import server.Server;
import server.User;

public class AssassinsSession extends GameSession {

	public AssassinsSession(String gameType, User owner, Server server) {
		super(gameType, owner, server);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(String message, User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLocation(Location loc, User user) {
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
	public void addUser(User user, int teamid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBeacon(int teamid, Location loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeBeacon(int teamid, Integer id) {
		// TODO Auto-generated method stub

	}

}
