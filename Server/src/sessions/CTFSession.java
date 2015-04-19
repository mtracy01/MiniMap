package sessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import server.Beacon;
import server.CTFUser;
import server.Location;
import server.Server;
import server.Team;
import server.User;
import server.Utility;
// TODO: make changes specific to CTF
public class CTFSession extends GameSession {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	public HashMap<User, CTFUser> ctfusers = new HashMap<User, CTFUser>();
	private Location startLoc;
	private Location endLoc; // This is for endpoints of line of scrimmage
	private Location flag2loc;
	private Location flag3loc;
	public HashMap<Integer, Integer> sides = new HashMap<Integer, Integer>();
	
	/**
	 * Store any potential finds
	 */
	private ArrayList<PotentialFind> potentialFinds;
	
	public CTFSession(User owner, Server server) {
		super("ctf", owner, server);
		teams.add(new Team(2));
		teams.add(new Team(3)); //THIS SHOULD BE AUTOMATED
		potentialFinds = new ArrayList<PotentialFind>();
		//There is only one team in a friend finder session
		// TODO: Add the owner to a team
	}

	@Override
	public void handleMessage(String message, User user) {
		// TODO Auto-generated method stub
		
		String[] messageParts = message.split(" ");
		switch(messageParts[0]) {
			//User reporting location to teammates
			case "confirmTagged":
				synchronized (users) {
					// Get the potentialFind for the user
					PotentialFind find = null;
					for (PotentialFind f : potentialFinds) {
						if (f.tagged.equals(user)) {
							find = f;
							break;
						}
					}
					if (find == null) {
						return;
					}
					
					if (messageParts[1].equals("true")) {
						// The tagged confirmed
						find.taggedConfirm = true;
						if (find.bothConfirmed()) {
							processTag(find);
						}
					} else {
						// Reject happened, remove any potential find
						potentialFinds.remove(find);
					}
				}
				break;
			case "confirmTag":
				synchronized (users) {
					// Get the potentialFind for the user
					PotentialFind find = null;
					for (PotentialFind f : potentialFinds) {
						if (f.tagger.equals(user)) {
							find = f;
							break;
						}
					}
					if (find == null) {
						return;
					}					
					
					if (messageParts[1].equals("true")) {
						// The assassin confirmed
						find.taggerConfirm = true;
						if (find.bothConfirmed()) {
							processTag(find);
						}
					} else {
						// Reject happened, remove any potential find
						potentialFinds.remove(find);
					}
				}
				break;
			case "flag":
				Location loc = new Location(Double.parseDouble(messageParts[2]), Double.parseDouble(messageParts[3]));
				if (Integer.parseInt(messageParts[1]) == 2) {
					flag2loc = loc;
					sides.put(checkSide(loc), 2);
				}
				else {
					flag3loc = loc;
					sides.put(checkSide(loc), 3);
				}			
				break;
			case "lineOfScrimmage":
				startLoc = new Location(Double.parseDouble(messageParts[1]), Double.parseDouble(messageParts[2]));
				endLoc = new Location(Double.parseDouble(messageParts[3]), Double.parseDouble(messageParts[4]));			
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
			ctfusers.put(user, new CTFUser(user));
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
		ctfusers.remove(user);
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
		ctfusers.put(user, new CTFUser(user));
		getTeambyID(teams, teamid).addUser(user);
		synchronized (users) {
			users.add(user);
		}
	}
	
	private void processTag(PotentialFind find) {
		// Send out the global kill message
		String tagMessage = "tag " + find.tagger.getUserID() + " " + find.tagged.getUserID();
		ctfusers.get(find.tagged).setInJail(true);
		for (User u : users) {
			u.sendMessage(tagMessage);
		}
		potentialFinds.remove(find);		
		// Remove a potential find where the target is the assassin
		PotentialFind toRemove = null;
		for (PotentialFind f : potentialFinds) {
			if (f.tagger.equals(find.tagged)) {
				toRemove = f;
				break;
			}
		}
		if (toRemove != null) {
			potentialFinds.remove(toRemove);
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
				User finder = user;
				User found = u;
				boolean close = Utility.areClose(user, u, Utility.PROXIMITY_DISTANCE);
				
				if(close){
					
					double check1 = checkSide(user.getLocation());
					double check2 = checkSide(u.getLocation());
					if (check1 * check2 < 0) {
						break; // players are on the opposite sides of the line of scrimmage, no one tags anyone
					}
					
					
					if (!potentialFinds.contains(found)) {
						//send message to each user asking to accept				
						if (check1 > 0) { // both on positive side
							if (user.getTeamID() == sides.get(1)) { 
								user.sendMessage("acceptTag " + u.getUserID());
								u.sendMessage("acceptTagged " + user.getUserID());
								
							}
							else {
								user.sendMessage("acceptTagged " + u.getUserID());
								u.sendMessage("acceptTag " + user.getUserID());																
							}
							
						}
						else { // both on negative side
							if (user.getTeamID() == sides.get(-1)) {	
								user.sendMessage("acceptTag " + u.getUserID());
								u.sendMessage("acceptTagged " + user.getUserID());								
							}
							else {
								user.sendMessage("acceptTagged " + u.getUserID());
								u.sendMessage("acceptTag " + user.getUserID());																
							}							
						}
						
						break; 					
					}						
				}
			}
		}

	}
	
	public void accept(User user) {
		synchronized (users) {
			ctfusers.put(user,  new CTFUser(user));
			users.add(user);
		}
		log.fine("users: " + users);
		if (user.isInGame()) {
			user.getGameSession().removeUser(user);
		}
		user.setGameSession(this);
		user.setInGame(true);
		sendSessionUsers();
	}
	
	
	class PotentialFind {
		public User tagger;
		public User tagged;
		public boolean taggerConfirm;
		public boolean taggedConfirm;
		
		public boolean bothConfirmed() {
			return taggerConfirm && taggedConfirm;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((tagger == null) ? 0 : tagger.hashCode());
			result = prime * result
					+ ((tagged == null) ? 0 : tagged.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PotentialFind other = (PotentialFind) obj;
			if (tagger == null) {
				if (other.tagger != null)
					return false;
			} else if (!tagger.equals(other.tagger))
				return false;
			if (tagged == null) {
				if (other.tagged != null)
					return false;
			} else if (!tagged.equals(other.tagged))
				return false;
			return true;
		}
		
		
	}
	
	
	/**
	 * 
	 * @param loc
	 * @return given a location, returns a negative value for being on one side of the line of scrimmage
	 * positive for the other side
	 */
	public int checkSide(Location loc) {
		double[] v1 = {getEndLoc().getLatitude() - getStartLoc().getLatitude(), getEndLoc().getLongitude() - getStartLoc().getLongitude()};
		double[] v2 = {getEndLoc().getLatitude() - loc.getLatitude(), getEndLoc().getLongitude() - loc.getLongitude()};
		if (v1[0] * v2[1] - v1[1] * v2[0] > 0) {
			return 1;
		}
		return -1;
		
	
	}



	public Location getStartLoc() {
		return startLoc;
	}

	public Location getEndLoc() {
		return endLoc;
	}
}
