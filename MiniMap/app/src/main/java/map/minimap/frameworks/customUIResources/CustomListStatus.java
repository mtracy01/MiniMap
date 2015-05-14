package map.minimap.frameworks.customUIResources;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import map.minimap.R;

/**
 * Created by Matthew on 4/6/2015.
 * Custom list for friend status fragment
 */
public class CustomListStatus extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web;                //String of user names
    private final Bitmap[] imageId;           //Bitmaps of user images
    private final boolean[] isOnline;        //Status of whether user is online or not
    public CustomListStatus(Activity context,
                            String[] web, Bitmap[] imageId, boolean[] isOnline) {
        super(context, R.layout.list_single_status, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.isOnline = isOnline;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single_status, null, true);

        //Get views of text, image, and status radio
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        RadioButton radioButton = (RadioButton) rowView.findViewById(R.id.radio);

        //Set text, photo, and status indicator appropriately
        txtTitle.setText(web[position]);
        imageView.setImageBitmap(imageId[position]);
        radioButton.setClickable(false);                    //Do not allow people to click the radio button, it is just a status indicator
        radioButton.setHighlightColor(Color.GREEN);
        if (isOnline[position] == true)
            radioButton.setChecked(true);
        return rowView;
    }
}
