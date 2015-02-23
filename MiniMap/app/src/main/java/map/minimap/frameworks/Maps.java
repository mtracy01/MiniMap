package map.minimap.frameworks;

import android.content.Context;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Matthew on 2/21/2015.
 * Purpose: Helper class for implementation of the MapFragment
 *          used by all games.
 */
public class Maps {

    //Debug variables
    //private static String LOG_TAG= "Maps Helper";

    //Elements of player fields
    private static int height;
    private static int width;



    //List of users in the game
    private static User[] users;
    private static LatLng center;

    /**
     * Purpose: Select the type of overlay to use for game map
     * @param map : The GoogleMap object from the Activity's
     *              implementation of OnMapReady
     * @param mapType: The type of overlay to use
     */
    public static void setMapType(GoogleMap map, int mapType){
        switch(mapType){
            case 0:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
            case 1:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            case 2:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            case 3:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            case 4:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }

    }

    /**
     * Purpose: Specify borders for map
     * @param h Diameter of the north-south dimension of the map
     * @param w Diameter of the east-west dimension of the map
     */
    public static void setBorders(int h, int w){
        height=h;
        width=w;
    }

    /**
     * Purpose: Get the x and y coordinates of our user and set his coordinates to be the center of the map.
     * @param user host user
     */
    public static void setCenterPosition(User user){
        center=user.getCoordinates();
    }

    /**
     * Purpose: Prepare map with specified parameters
     * @param map the map that is being modified by this framework
     */
    public static void readyMap(GoogleMap map){
        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }

    /**
     * Purpose: Initialize markers for players and gather list
     *          of players
     * @param map the map that is being modified by this framework
     * @param playerList the list of players in the game
     */
    public static void initializePlayers(GoogleMap map, User[] playerList){
        int length = playerList.length;

        /* create copy of users to store in class */
        users= new User[length];
        System.arraycopy(playerList,0,users,0,length);


        /* Create markers and put them in respective locatons */
        for(int i=0;i<length;i++){

            /* Convert coordinates to latitude and longitude tuple */
            LatLng latLng = users[i].getCoordinates();




            switch (users[i].getTeam()){
                case 0:
                    /* Decode profile picture by calling Facebook Graph API */
                    users[i].setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(users[i].getUserImage()))));
                case 1:
                    users[i].setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                case 2:
                    users[i].setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                case 3:
                    users[i].setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
                case 4:
                    users[i].setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
            }
        }

    }

}
