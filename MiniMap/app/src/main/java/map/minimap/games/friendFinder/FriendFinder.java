package map.minimap.games.friendFinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ext.SatelliteMenu;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import map.minimap.MainMenu;
import map.minimap.R;
import map.minimap.frameworks.coreResources.IDCipher;
import map.minimap.frameworks.gameResources.Game;
import map.minimap.frameworks.mapResources.Maps;
import map.minimap.frameworks.mapResources.SyncedMapFragment;
import map.minimap.helperClasses.Data;

public class FriendFinder extends FragmentActivity implements OnMapReadyCallback {

    private SyncedMapFragment map;
    private AppEventsLogger logger = AppEventsLogger.newLogger(this);

    private final int NOTHING_BEACON_MENU_ID = 0;
    private final int ADD_BEACON_MENU_ID = 1;
    private final int REMOVE_BEACON_MENU_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Data.gameActivity = this;
        logger.logEvent("Friend finder launched", Data.players.size());
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //Set content to our current layout
        setContentView(R.layout.activity_friend_finder);

        //Set up satellite menu
        android.view.ext.SatelliteMenu menu = (android.view.ext.SatelliteMenu) findViewById(R.id.menu);
        java.util.List<android.view.ext.SatelliteMenuItem> items = new java.util.ArrayList<>();

        if (Data.user.getGame().isBeaconsEnabled()) {
            // TODO: Need to include the following in the google play store listing:
            // App icons by <a href="http://icons4android.com">Icons4Android</a>.
            items.add(new android.view.ext.SatelliteMenuItem(REMOVE_BEACON_MENU_ID, R.drawable.sat_remove_beacon));
            items.add(new android.view.ext.SatelliteMenuItem(ADD_BEACON_MENU_ID, R.drawable.sat_add_beacon));
            items.add(new android.view.ext.SatelliteMenuItem(NOTHING_BEACON_MENU_ID, R.drawable.sat_map));
        }
        menu.addItems(items);
        menu.setOnItemClickedListener(new SatelliteMenu.SateliteClickedListener() {
            @Override
            public void eventOccured(int id) {
                switch (id) {
                    case NOTHING_BEACON_MENU_ID:
                        Data.user.getGame().setBeaconMode(Game.BeaconMode.NOTHING);
                        break;
                    case ADD_BEACON_MENU_ID:
                        Data.user.getGame().setBeaconMode(Game.BeaconMode.ADD);
                        break;
                    case REMOVE_BEACON_MENU_ID:
                        Data.user.getGame().setBeaconMode(Game.BeaconMode.REMOVE);
                        break;
                }
            }
        });

        //Initialize our map fragment
        if (savedInstanceState == null) {
            map = new SyncedMapFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.map, map).commit();
            Data.mapFragment=map;

            Data.mapFragment.getMapAsync(this);

        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit the game?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        FriendFinder.super.onBackPressed();
                        Data.client.sendMessage("remove " + Data.gameId + " " + IDCipher.toCipher(Data.user.getID()));
                        Data.gameStarted = false;
                        startActivity(new Intent(FriendFinder.this,MainMenu.class));
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_finder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Maps.readyMap(map);
    }
}
