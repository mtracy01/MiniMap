package map.minimap.games.captureTheFlag;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import map.minimap.MainMenu;
import map.minimap.R;
import map.minimap.frameworks.mapResources.Maps;
import map.minimap.frameworks.mapResources.SyncedMapFragment;
import map.minimap.helperClasses.Data;
import map.minimap.mainMenuComponents.LobbyFragment;

/**
 * Created by Corey on 4/18/2015.
 */
public class CTFflags extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    public double latitude;
    private boolean Is_MAP_Moveable = false;
    public double longitude;
    private LatLng flag1;
    private LatLng flag2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_ctfscrimmage);

        FrameLayout fram_map = (FrameLayout) findViewById(R.id.fram_map);
        Button done = (Button) findViewById(R.id.scrim_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag1==null || flag2==null){
                    Toast toast = Toast.makeText(getApplicationContext(), "Put the flags down", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Data.client.ctfFlags(Double.toString(flag1.latitude), Double.toString(flag1.longitude), "2");
                    Data.client.ctfFlags(Double.toString(flag1.latitude), Double.toString(flag1.longitude), "3");
                    swap_Activity();
                }
            }});
        Button clearLine = (Button) findViewById(R.id.clear_line);
        clearLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                flag1 = null;
                flag2 = null;
            }});

        Button btn_draw_State = (Button) findViewById(R.id.btn_draw_State);

        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Is_MAP_Moveable != true) {
                    Is_MAP_Moveable = true;
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                } else {
                    Is_MAP_Moveable = false;
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                }
            }
        });

        SyncedMapFragment customMapFragment = ((SyncedMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mMap = customMapFragment.getMap();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Data.user.getCoordinates(), 18));
        Toast toast = Toast.makeText(getApplicationContext(), "Place flag one, then Flag two. Hit clear to clear both and done when complete.", Toast.LENGTH_LONG);
        toast.show();
        Intent intent = getIntent();
        if(intent.hasExtra("ctf")) {
            String scrimLine = intent.getStringExtra("ctf");
            String[] parts = scrimLine.split(" ");
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(Double.parseDouble(parts[0]),Double.parseDouble(parts[1])), new LatLng(Double.parseDouble(parts[2]),Double.parseDouble(parts[3]))).color(Color.BLUE).width(5));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                Log.d("Map","Map clicked");
                if(Is_MAP_Moveable) {
                    if(flag1==null)
                        flag1=point;
                    else{
                        if (flag2 != null) {
                            Toast toast2 = Toast.makeText(getApplicationContext(), "Already placed both flags. Hit clear to place them again.", Toast.LENGTH_SHORT);
                            toast2.show();
                        } else {
                            flag2 = point;
                        }
                    }
                    Draw_Map();
                }
            }
        });

    }
    public void swap_Activity() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra("ctf", "scrim line done");
        startActivity(intent);

    }
    public void Draw_Map() {
        if(flag2==null)
            mMap.addMarker(new MarkerOptions()
                    .position(flag1)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        else{
            mMap.addMarker(new MarkerOptions()
                    .position(flag2)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
                        CTFflags.super.onBackPressed();
                        startActivity(new Intent(CTFflags.this, MainMenu.class));
                    }
                }).create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_assassins, menu);
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

