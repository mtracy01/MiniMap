package sessions;

import java.util.ArrayList;
import java.util.logging.Logger;

import server.Beacon;
import server.CTFUser;
import server.Location;
import server.Server;
import server.Team;
import server.User;
import server.Utility;
// TODO: make changes specific to Sardines
public class CTFSession extends GameSession {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	/**
	 * Store any potential finds
	 */
	private ArrayList<CTFUser> potentialFinds;
	
	public CTFSession(User owner, Server server) {
		super("ctf", owner, server);
		teams.add(new Team(2));
		teams.add(new Team(3)); //THIS SHOULD BE AUTOMATED
		potentialFinds = new ArrayList<CTFUser>();
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
			case "confirmTagged":
				CTFUser u = (CTFUser) user;
				u.setInJail(true);
				if (u.hasFlag()) {
					u.setHasFlag(false);
				}
				for (User player: users) {
					player.sendMessage("flagReturned " + u.getUserID());
				}
				
				break;
			case "confirmTag":
				break;
			case "flag":
				break;
			case "lineOfScrimmage":
				break;
				
		}
	}
	
	@Override
	/* called when user presses start button
	 * assign teams, etc...
	 *  */
	public void startSession() {
		log.fine("Starting ctf session " + this.getId());
		isRunning = true;
		
		int lastteam = 0;
		for (User user: this.users)
		{
			//this should add players to alternating teams
			teams.get(lastteam % 2).addUser(user);
			lastteam++;
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
		log.finer("Removing user from ctf session");
		user.setInGame(false);
		user.setGameSession(null);
		if (getTeambyID(teams, user.getTeamID()) != null) {
			getTeambyID(teams, user.getTeamID()).removeUser(user);
		}
		synchronized (users) {
			log.finer(users.toString());
			
			// Actually remove the user
			users.remove(user);
			
			// Send the remove message to all users, including the one getting removed
			String removeMessage = "userRemoved " + user.getUserID();
			for (User u : users) {
				u.sendMessage(removeMessage);
			}
			user.sendMessage(removeMessage);
			
			log.finer(users.toString());
			log.finer(users.size() + " users in session");
			
			// Check for empty sessions
			if (users.isEmpty()) {
				endSession();
			}
			// Check for owner succession
			if (owner.equals(user) && !users.isEmpty()) {
				owner = users.iterator().next();
			}
		}
		sendSessionUsers();
	}

	@Override
	/* mid-game */
	public void addUser(User user, int teamid) {
		// TODO Auto-generated method stub
		user.setTeamID(teamid);
		CTFUser u = new CTFUser(user.getSocket(), user.getServer());
		getTeambyID(teams, teamid).addUser(u);
		synchronized (users) {
			users.add(u);
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
	
		log.fine("User: " + user);
		log.fine("Team: " + teams.get(0));
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
			if (u != null) {
				u.sendMessage(m.toString());
			}
		}
		
		//send confirmation message 
		if (otid == 0) {
			for (User u: this.teams.get(otid).getUsers())
			{
				if (u == null) {
					continue;
				}
				// user finds person U
				CTFUser finder = (CTFUser) user;
				CTFUser found = (CTFUser) u;
				boolean close = Utility.areClose(user, u, Utility.PROXIMITY_DISTANCE);
				
				if(close){
					if (!potentialFinds.contains(found)) {
						//send message to each user asking to accept
				
						found.sendMessage("acceptTagged " + u.getUserID());
						finder.sendMessage("acceptTag " + u.getUserID());
	
						break; 
					
					}	
					
				}
			}
		}

	}
	
	public void accept(User user) {
		CTFUser u;
		synchronized (users) {
			u = new CTFUser(user.getSocket(), user.getServer());
			users.add(u);
		}
		log.fine("users: " + users);
		if (u.isInGame()) {
			u.getGameSession().removeUser(u);
		}
		u.setGameSession(this);
		u.setInGame(true);
		sendSessionUsers();
	}
	
	
	
}
