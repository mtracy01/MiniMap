package map.minimap.frameworks;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import map.minimap.helperClasses.Data;

public class Beacon {

	private int teamID;
	private LatLng location;
	private int beaconID;
    private Marker mapMarker;
	
	public Beacon (final LatLng loc, int beaconID, int teamID)
	{
		setLocation(loc);
		this.teamID = teamID;
        this.beaconID = beaconID;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            public void run() {
                mapMarker = Data.map.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        });
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}
	
	public int getBeaconID() {
		return beaconID;
	}
	
	public int getTeamID() {
		return teamID;
	}
	
	public void setTeamID(int id) {
		this.teamID = id;
	}

    public void removeBeacon() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            public void run() {
                mapMarker.remove();
            }
        });

    }
}
