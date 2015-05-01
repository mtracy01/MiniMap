package map.minimap.games.marcoPolo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import map.minimap.R;
import map.minimap.frameworks.mapResources.Maps;
import map.minimap.frameworks.mapResources.SyncedMapFragment;
import map.minimap.helperClasses.Data;

public class MarcoPolo extends ActionBarActivity implements OnMapReadyCallback{

    private SyncedMapFragment map;
    private AppEventsLogger logger = AppEventsLogger.newLogger(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Data.gameActivity = this;
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        logger.logEvent("Marco Polo launched", Data.players.size());

        setContentView(R.layout.activity_marco_polo);


        //Set up satellite menu, add elements
        android.view.ext.SatelliteMenu menu = (android.view.ext.SatelliteMenu) findViewById(R.id.menu);
        java.util.List<android.view.ext.SatelliteMenuItem> items = new java.util.ArrayList<>();

        if (Data.user.getTeam() == 2) {//if is marco
            //add marco button
            items.add(new android.view.ext.SatelliteMenuItem(5, R.drawable.sat_item));
        }

        items.add(new android.view.ext.SatelliteMenuItem(4,R.drawable.sat_item));
        items.add(new android.view.ext.SatelliteMenuItem(4,R.drawable.sat_item));
        items.add(new android.view.ext.SatelliteMenuItem(4,R.drawable.sat_item));
        items.add(new android.view.ext.SatelliteMenuItem(3,R.drawable.sat_item));
        items.add(new android.view.ext.SatelliteMenuItem(2,R.drawable.sat_item));
        items.add(new android.view.ext.SatelliteMenuItem(1,R.drawable.sat_item));
        menu.addItems(items);

        menu.setOnItemClickedListener(new SatelliteMenu.SateliteClickedListener() {
            @Override
            public void eventOccured(int id) {
                switch (id) {
                    case 5:
                        Data.client.sendMessage("Marco");
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
    public void onMapReady(GoogleMap map) {
        Maps.readyMap(map);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_marco_polo, menu);
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
}
