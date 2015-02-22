package map.minimap.frameworks;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.net.URL;

import map.minimap.R;

/**
 * Created by Matthew on 2/21/2015.
 * Purpose: Helper class for implementation of the MapFragment
 *          used by all games.
 */
public class Maps {

    //Debug variables
    private static String LOG_TAG= "Maps Helper";

    //Elements of player fields
    private static int height;
    private static int width;



    //List of users in the game
    private static User[] users;

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
     * @param h
     * @param w
     */
    public static void setBorders(int h, int w){
        height=h;
        width=w;
    }

    /**
     * Purpose: Prepare map with specified parameters
     * @param map
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
     * @param map
     * @param playerList
     */
    public static void initializePlayers(GoogleMap map, User[] playerList, Context context){
        int length = playerList.length;

        /* create copy of users to store in class */
        users= new User[length];
        System.arraycopy(playerList,0,users,0,length);

        /* Get metadata so we can use Facebook API key */
        String apiKey="";
        try{
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("map.minimap",PackageManager.GET_META_DATA);
            Bundle bunderu = applicationInfo.metaData;
            apiKey= bunderu.getString("com.facebook.sdk.ApplicationId");
        }
        catch(PackageManager.NameNotFoundException e){
            Log.e(LOG_TAG,"Failed to load meta-data, NameNotFound" + e.getMessage());
        }
        catch(NullPointerException e){
            Log.e(LOG_TAG,"Failed to load meta-data, NullPointer" + e.getMessage());
        }


        /* Create markers and put them in respective locatons */
        for(int i=0;i<length;i++){

            /* Convert coordinates to latitude and longitude tuple */
            LatLng latLng = new LatLng(users[i].getXcoord(),users[i].getYcoord());

            /* Decode profile picture by calling Facebook Graph API */
            Bitmap userIcon;
            try{
                URL img_value = null;
                img_value = new URL("http://graph.facebook.com/"+apiKey+"/picture?type=small");
                userIcon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());

            }
            catch(Exception e){
                Log.e(LOG_TAG,"Failed to get User image" + e.getMessage());
                return;
            }


            switch (users[i].getTeam()){
                case 0:
                    /*users[i].setMarker(map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))));*/
                    users[i].setMarker(map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(userIcon))));
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
