package map.minimap.frameworks.gameResources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import map.minimap.R;
import map.minimap.helperClasses.Data;

/**
 * Created by nickiogg on 4/18/15.
 */
public class Flag {

    private final float[] TEAM_COLORS = { BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_GREEN };

    private LatLng location;
    private Team team;
    private Marker mapMarker;

    public Flag(LatLng location, Team team) {
        this.location = location;
        this.team = team;
    }

    public void show() {
        Bitmap flagImage;
        if (this.team.getTeamID() == 2) {
            flagImage = BitmapFactory.decodeResource(Data.mainAct.getResources(), R.drawable.ic_flag_blue);
        } else if (this.team.getTeamID() == 3) {//red
            flagImage = BitmapFactory.decodeResource(Data.mainAct.getResources(), R.drawable.ic_flag_red);
        }
        else {
            Log.e("Flag Picture", "Invalid team ID");
            flagImage = BitmapFactory.decodeResource(Data.mainAct.getResources(), R.drawable.ic_flag_red);
        }

        final Bitmap tmp = flagImage;

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            public void run() {

                mapMarker = Data.map.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(tmp)));
            }
        });
    }

    public void hide() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            public void run() {
                mapMarker.remove();
            }
        });
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }



}
