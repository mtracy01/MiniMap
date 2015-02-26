package server;

public class Location {
	private double longitude;
	private double latitude;
	
	public Location (double lon, double lat)
	{
		setLongitude(lon);
		setLatitude(lat);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	
}
