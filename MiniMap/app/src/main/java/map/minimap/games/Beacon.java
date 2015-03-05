package map.minimap.games;

import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;

public class Beacon {

	private int teamid;
	private LatLng location;
	private int id;
	
	public Beacon (LatLng loc, int id)
	{
		setLocation(loc);
		this.id = id;
		
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}
	
	public int getId() {
		return id;
	}
	
	public int getTeamId() {
		return teamid;
	}
	
	public void setTeamId(int id) {
		this.teamid = id;
	}
}
