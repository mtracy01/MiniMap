package map.minimap.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import map.minimap.MainMenu;
import map.minimap.R;
import map.minimap.frameworks.MapResources.Maps;
import map.minimap.frameworks.MapResources.SyncedMapFragment;
import map.minimap.helperClasses.Data;

/**
 * Created by Corey on 4/18/2015.
 */
public class CTFscrimmage extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    public static boolean mMapIsTouched = false;
    SyncedMapFragment customMapFragment;
    Projection projection;
    public double latitude;
    private boolean Is_MAP_Moveable = false;
    public double longitude;
    private SyncedMapFragment map;
    private ArrayList<LatLng> val = new ArrayList<>();
    private AppEventsLogger logger = AppEventsLogger.newLogger(this);
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
                Data.client.createScrimmageLineMessage(Double.toString(val.get(0).latitude),Double.toString(val.get(0).longitude),
                        Double.toString(val.get(val.size()-1).latitude),Double.toString(val.get(val.size()-1).longitude));
                swap_Activity();
            }});
        Button clearLine = (Button) findViewById(R.id.clear_line);
        clearLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
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



        customMapFragment.setOnDragListener(new SyncedMapFragment.MapViewWrapper.OnDragListener() {
            @Override
               public void onDrag(MotionEvent motionEvent) {
            Log.i("ON_DRAG", "X:" + String.valueOf(motionEvent.getX()));
            Log.i("ON_DRAG", "Y:" + String.valueOf(motionEvent.getY()));

            float x = motionEvent.getX();
            float y = motionEvent.getY();

            int x_co = Integer.parseInt(String.valueOf(Math.round(x)));
            int y_co = Integer.parseInt(String.valueOf(Math.round(y)));

            projection = mMap.getProjection();
            Point x_y_points = new Point(x_co, y_co);
            LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
            latitude = latLng.latitude;
            longitude = latLng.longitude;

            Log.i("ON_DRAG", "lat:" + latitude);
            Log.i("ON_DRAG", "long:" + longitude);
                if(Is_MAP_Moveable) {
                    val.add(latLng);
                    Draw_Map();
                }

            // Handle motion event:
        }
        });
    }
    public void swap_Activity() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra("ctf", "scrim line done");
        startActivity(intent);

    }
    public void Draw_Map() {
        Polyline rectOptions = mMap.addPolyline(new PolylineOptions()
        .add(val.get(0),val.get(val.size()-1)). color(Color.BLUE).width(5));

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit the game?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        CTFscrimmage.super.onBackPressed();
                        startActivity(new Intent(CTFscrimmage.this, MainMenu.class));
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

