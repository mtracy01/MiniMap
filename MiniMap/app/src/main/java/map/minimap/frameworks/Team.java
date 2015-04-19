package map.minimap.frameworks;

import java.util.ArrayList;

public class Team {
	
	
	/**
	 * Store the players in the team
	 */
	private ArrayList<User> users;
	private int teamID;
	private int id;

    // WARNING: This is not used, the beacon list in User is used instead
	private ArrayList<Beacon> beacons;
	
	public Team(int tid) {
		users = new ArrayList<>();
		this.teamID = tid;
		beacons = new ArrayList<>();
	}
	
	/**
	 * Add a user to the team
	 * @param u
	 */
	public void addUser(User u) {
		synchronized (users) {
			users.add(u);
		}
	}
	
	public void addBeacon(Beacon b) {
		synchronized (beacons) {
			beacons.add(b);
		}
	}

	public Beacon getBeaconbyID(int id) {
		for (Beacon b: beacons) {
			if (b.getBeaconID() == id) {
				return b;
			}
		}
		return null;
	}
	
	/**
	 * Remove a user from the team
	 * @param u
	 */
	public void removeUser(User u) {
		synchronized (users) {
			users.remove(u);
		}
	}
	
	public void removeBeacon(Beacon b) {
		synchronized (beacons) {
			beacons.remove(b);
		}
	}

    public boolean containsUser(User user) {
        for (User u : users) {
            if (u.getID().equals(user.getID())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Beacon> getBeacons() {
        return beacons;
    }

    public void sendMessage(String message) {
		for (User u : users) {
			//u.sendMessage(message);
		}
	}
	
	public int getTeamID() {
		return teamID;
	}
	
	//Added for endSession: Can't access list of beacons otherwise to delete them all
	public void removeAllBeacons()
	{
		for (Beacon beacon: this.beacons)
		{
			removeBeacon(beacon);
		}
	}
	
}
