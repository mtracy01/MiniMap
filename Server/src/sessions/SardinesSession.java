package sessions;

import java.util.logging.Logger;

import server.Beacon;
import server.Location;
import server.Server;
import server.Team;
import server.User;
import server.Utility;
// TODO: make changes specific to Sardines
public class SardinesSession extends GameSession {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	public SardinesSession(User owner, Server server) {
		super("sardines", owner, server);
		teams.add(new Team(2));
		teams.add(new Team(3)); //THIS SHOULD BE AUTOMATED
		//There is only one team in a friend finder session
		// TODO: Add the owner to a team
	}

	@Override
	public void handleMessage(String message, User user) {
		// TODO Auto-generated method stub
		
		String[] messageParts = message.split(" ");
		StringBuilder m = new StringBuilder();
		switch(messageParts[0]) {
		//User reporting location to teammates
		case "Found":
			if (messageParts[2].equals("true"))
			{
				m.append("TeamChange");
				m.append(" " + user.getUserID());
				m.append(" " + teams.get(0).getTeamID());
				teams.get(1).sendMessage(m.toString());
				
				User temp = server.getUserByID(messageParts[1]);
				teams.get(1).removeUser(temp);
				teams.get(0).addUser(temp);
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
		
		//First user is Hidden 
		//Others are Seekers
		boolean hiddenChosen = false;
		for (User user: this.users)
		{
			if (!hiddenChosen)
			{
				teams.get(0).addUser(user);
				hiddenChosen = true;
			}
			else
				teams.get(1).addUser(user);
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
		
		synchronized (users) {
			Object[] userArray = users.toArray();
			for (Object u : userArray) {
				removeUser((User) u);
			}
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
		log.finer("Removing user from sardines session");
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
				owner = users.iterator().next();
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

	@Override
	public void handleLocation(Location loc, User user) {
		// TODO Auto-generated method stub
		
		StringBuilder m = new StringBuilder();
		//send location to all users for them to handle
		m.append("location");
		m.append(" " + user.getUserID());
		m.append(" " + loc.getLatitude());
		m.append(" " + loc.getLongitude());
		m.append(" " + user.getTeamID());
		
		int tid; //friendly team
		int otid; //opposing team
	
		if (teams.get(0).contains(user))
		{
			tid = 0;
			otid = 1;
		}
		else
		{
			tid = 1;
			otid = 0;
		}
			
		for (User u: this.teams.get(tid).getUsers())
		{
			u.sendMessage(m.toString());
		}
		
		//send confirmation message to sardines that are close by
		for (User u: this.teams.get(otid).getUsers())
		{
			
			boolean close = Utility.areClose(user, u, Utility.PROXIMITY_DISTANCE);
			
			if(close){
				StringBuilder n = new StringBuilder();
				//send location to all users for them to handle
				n.append("Found");
				n.append(" " + user.getUserID());
				
				u.sendMessage(n.toString());
				break; //only send it to one person.  No need to spam
			}
		}

	}
	
}
