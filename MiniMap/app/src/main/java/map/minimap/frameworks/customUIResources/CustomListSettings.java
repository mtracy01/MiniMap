package map.minimap.frameworks.customUIResources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import map.minimap.LoginActivity;
import map.minimap.R;
import map.minimap.helperClasses.FacebookHelper;

/**
 * Created by Matthew on 5/6/2015.
 * Custom list for setting toggles
 */
public class CustomListSettings extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] settingTitles;
    public CustomListSettings(Activity context,String[] settingTitles) {
        super(context, R.layout.list_single_invite, settingTitles);
        this.context = context;
        this.settingTitles = settingTitles;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final int rowPosition = position;
        final WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_settings, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        final Switch   switc    = (Switch)   rowView.findViewById(R.id.toggle);

        txtTitle.setText(settingTitles[position]);

        //Set up prior state of switch
        switch(position){
            case 0: //Toggle wifi switch
                //If we are connected to wifi, set switch state to on, if not, set to off
                if(wifiManager.isWifiEnabled())
                    switc.setChecked(true);
                else
                    switc.setChecked(false);
                break;
        }

        //Set up onClickListener for settings switches
        switc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(rowPosition){
                    case 0:     //WiFi toggle
                        if(wifiManager.isWifiEnabled()){

                            final AlertDialog.Builder warning = new AlertDialog.Builder(context);
                            warning.setTitle("Warning!");
                            warning.setMessage("Disabling wifi will require you to log back into the client.  Continue?");

                            //Disable wifi requires us to login again to prevent client issues
                            warning.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switc.setChecked(false);
                                    wifiManager.setWifiEnabled(false);
                                    FacebookHelper.logout();
                                    Toast.makeText(context, "Wifi disabled", Toast.LENGTH_SHORT).show();
                                    getContext().startActivity(new Intent(context, LoginActivity.class));
                                }
                            });

                            warning.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switc.setChecked(true);
                                    dialog.cancel();

                                }
                            });

                            warning.show();

                        }
                        else{
                            wifiManager.setWifiEnabled(true);
                            switc.setChecked(true);
                            Toast.makeText(context,"Wifi enabled",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });




        return rowView;
    }
}