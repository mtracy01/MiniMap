package map.minimap.frameworks.customUIResources;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import map.minimap.R;

/**
 * Created by Matthew on 5/6/2015.
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
                            wifiManager.setWifiEnabled(false);
                            switc.setChecked(false);
                            Toast.makeText(context,"Wifi disabled",Toast.LENGTH_SHORT).show();
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