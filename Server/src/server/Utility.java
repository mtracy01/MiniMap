package server;

import java.util.logging.Logger;

public class Utility {
	
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	
	public static final double PROXIMITY_DISTANCE = 25;

	/**
	 * Returns true if the two users are distance of each other
	 * @param user1
	 * @param user2
	 * @param distance
	 * @return
	 */
	public static boolean areClose(User user1, User user2, double distance) {
		if (user1.getLocation() == null || user2.getLocation() == null) {
			return false;
		}
		Location loc1 = user1.getLocation();
		Location loc2 = user2.getLocation();
		Double lat1, lat2, lon1, lon2;
		
		lat1 = Math.toRadians(loc1.getLatitude());
		lon1 = Math.toRadians(loc1.getLongitude());
		lat2 = Math.toRadians(loc2.getLatitude());
		lon2 = Math.toRadians(loc2.getLongitude());
		double R = 20902231; //radius of earth in ft
		double a = Math.pow(Math.sin((lat2-lat1)/2.0), 2.0)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin((lon2-lon1)/2.0),2.0);
		double c = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R*c;
		log.fine("Distance is: " + d);
		if( d < distance){
			return true;
		}
		return false;
	}
}
