package map.minimap.frameworks;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import map.minimap.helperClasses.Data;

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
    private static boolean hasBorders;
    private static boolean calledInitialize=false;

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
        hasBorders=true;
    }

    /**
     * Purpose: Get the x and y coordinates of our user and set his coordinates to be the center of the map.
     * @param user host user
     */
    public static void setCenterPosition(User user)
    {
        calledInitialize=true;
        center=user.getCoordinates();
    }

    /**
     * Purpose: Prepare map with specified parameters
     * @param map the map that is being modified by this framework
     */
    public static void readyMap(GoogleMap map){

        if(calledInitialize){
            //enable my current location
            map.setMyLocationEnabled(true);
            //initialize players, setting their markers
            initializePlayers(map, Data.users);
            //Move map's camera and set zoom level.  I will make the zoom a variable later
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(Data.user.getCoordinates(),13));
            //addPlayersToField(map);
        }
        //if we don't initialize, call Sydney
        else {
            LatLng sydney = new LatLng(-33.867, 151.206);

            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

            map.addMarker(new MarkerOptions()
                    .title("Sydney")
                    .snippet("The most populous city in Australia.")
                    .position(sydney));
        }
        Data.map = map;
    }

    /**
     * Purpose: Initialize markers for players and gather list
     *          of players
     * @param map the map that is being modified by this framework
     * @param playerList the list of players in the game
     */
    public static void initializePlayers(GoogleMap map, ArrayList<User> playerList){
        int length = playerList.size();
        hasBorders=false;
        /* create copy of users to store in class */

        /* Create markers and put them in respective locatons */
        for(User user : Data.users){

            /* Convert coordinates to latitude and longitude tuple */
            LatLng latLng = user.getCoordinates();

            switch (user.getTeam()-1){
                case 0:
                    /* Decode profile picture by calling Facebook Graph API */
                    // user.setMarker(map.addMarker(new MarkerOptions()
                    //         .title(user.getName()).position(latLng)
                    //         .icon(BitmapDescriptorFactory
                    //                 .fromBitmap(user.getUserImage()))));
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                    break;
                case 1:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                    break;
                case 2:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                    break;
                case 3:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
                    break;
                default:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                    break;
            }
        }

    }
    /*private static void addPlayersToField(GoogleMap map){

        //User[] users = new User[Data.users.size()];
        //System.arraycopy(Data.users.size,0,users,0,length);
        //Data.users;
        for(int i=0;i<users.length;i++){
            map.addMarker(users[i].getMarker());
        }
    }*/

}
