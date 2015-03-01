package sessions;

import java.util.logging.Logger;

import server.Beacon;
import server.Location;
import server.Server;
import server.Team;
import server.User;

public class FriendFinderSession extends GameSession {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );

	public FriendFinderSession(User owner, Server server) {
		super("friendFinder", owner, server);
		teams.add(new Team()); //There is only one team in a friend finder session
		// TODO: Add the owner to a team
	}

	@Override
	public void handleMessage(String message, User user) {
		// TODO Auto-generated method stub
		
		String[] messageParts = message.split(" ");
		StringBuilder m = new StringBuilder();
		switch(messageParts[0]) {
		//User reporting location
		case "location":
			//send location to all users for them to handle
			m.append("location");
			m.append(" " + user.getId());
			m.append(" " + messageParts[1]);
			m.append(" " + messageParts[2]);
			
			for (User u: this.users)
			{
				u.sendMessage(m.toString());
			}
			break;
		}

	}
	
	@Override
	/* called when user presses start button
	 * assign teams, etc...
	 *  */
	public void startSession() {
		log.fine("Starting game session " + this.getId());
		isRunning = true;
		
		//Put all users on the same team for friendfinder
		Team team = new Team();
		for (User user: this.users)
		{
			team.addUser(user);
		}
		this.teams.add(team);
		
		
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
		
		// Remove ourselves
		server.removeSession(this);
	}

	@Override
	public void removeUser(User user) {
		log.finer("Removing user from friendfinder session");
		user.setInGame(false);
		user.setGameSession(null);
		if (getTeambyID(teams, user.getTeamID()) != null) {
			getTeambyID(teams, user.getTeamID()).removeUser(user);
		}
		synchronized (users) {
			log.finer(users.toString());
			users.remove(user);
			log.finer(users.toString());
			log.finer(users.size() + " users in session");
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
