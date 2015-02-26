package server;

import java.util.ArrayList;

public class Beacon {

	private static Integer baseId = 0;
	private Location location;
	private int id;
	
	public Beacon (Location loc)
	{
		setLocation(loc);
		
		synchronized (baseId) {
			id = baseId;
			baseId++;
		}
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public int getId() {
		return id;
	}
}
