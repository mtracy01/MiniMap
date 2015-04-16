package map.minimap.frameworks.MapResources;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import map.minimap.R;
import map.minimap.frameworks.User;
import map.minimap.helperClasses.Data;

/**
 * Created by Matthew on 2/21/2015.
 * Purpose: Helper class for implementation of the MapFragment
 *          used by all games.
 */
public class Maps {

    //Debug variables
    private static String LOG_TAG= "Maps";

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
                break;
            case 1:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 2:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 3:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 4:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
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
            map.setMyLocationEnabled(false);

            //initialize players, setting their markers
            initializePlayers(map, Data.players);
            //Move map's camera and set zoom level.  I will make the zoom a variable later
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(Data.user.getCoordinates(),18));
            Log.v("Maps", "Set Center");
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
        // Add a listener for map clicks, used to add beacons
        Data.map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                if (Data.user.getInGame()) {
                    if (Data.user.getGame().isBeaconsEnabled()) {
                        Data.client.sendMessage("addbeacon " + point.latitude + " " + point.longitude);
                    }
                }
            }
        });
    }

    /**
     * Purpose: Initialize markers for players and gather list
     *          of players
     * @param map the map that is being modified by this framework
     * @param playerList the list of players in the game
     */
    public static void initializePlayers(GoogleMap map, ArrayList<User> playerList){

        hasBorders=false;
        /* create copy of users to store in class */

        /* Create markers and put them in respective locatons */
        for(User user : Data.players){

            /* Convert coordinates to latitude and longitude tuple */
            LatLng latLng = user.getCoordinates();

            if(user.getProfilePhoto()==null) {
                Log.e(LOG_TAG,"User didn't have a profile photo! Giving them default profile picture...");
                Bitmap big = BitmapFactory.decodeResource(Data.mainAct.getResources(), R.drawable.com_facebook_profile_picture_blank_portrait);
                big = Bitmap.createScaledBitmap(big,big.getWidth() / 5,big.getHeight() / 5, false);
                user.setProfilePhoto(big);
            }
            Bitmap tmp = user.getProfilePhoto();
            Bitmap doubleSized = Bitmap.createScaledBitmap(tmp,tmp.getWidth() * 2,tmp.getHeight() * 2, false);

            switch (user.getTeam()-1){
                case 0:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(addBorder(doubleSized, Color.DKGRAY)))));//.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                    break;
                case 1:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(addBorder(doubleSized, Color.BLUE)))));
                    break;
                case 2:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(addBorder(doubleSized, Color.RED)))));
                    break;
                case 3:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(addBorder(doubleSized, Color.YELLOW)))));
                    break;
                default:
                    user.setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(addBorder(doubleSized, Color.GREEN)))));
                    break;
            }
        }

    }

    private static Bitmap addBorder(Bitmap original, int color) {
        // Create a copy of the image so we don't modify the original
        Bitmap picture = original.copy(Bitmap.Config.ARGB_8888, true);
        picture.setHasAlpha(true);

        int outerRadius = Math.min(picture.getHeight(), picture.getWidth()) / 2;
        int innerRadius = (int) (0.95 * outerRadius);

        int outerSquared = outerRadius * outerRadius;
        int innerSquared = innerRadius * innerRadius;

        for (int i = 0; i < picture.getWidth(); i++) {
            for (int j = 0; j < picture.getHeight(); j++) {
                int x = (picture.getWidth() / 2) - i;
                int y = (picture.getHeight() / 2) - j;
                int radiusSquared = x*x + y*y;

                if (radiusSquared >= innerSquared && radiusSquared < outerSquared) {
                    // Add the border
                    picture.setPixel(i, j, color);
                } else if (radiusSquared >= outerSquared) {
                    // Set to transparent
                    picture.setPixel(i, j, Color.alpha(Color.TRANSPARENT));

                }
            }
        }

        return picture;
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
