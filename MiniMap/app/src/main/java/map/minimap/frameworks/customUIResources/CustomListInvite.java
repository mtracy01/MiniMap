package map.minimap.frameworks.customUIResources;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import map.minimap.R;
import map.minimap.helperClasses.Data;

/**
 * Created by Matthew on 4/5/2015.
 */
public class CustomListInvite extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web;
    private final Bitmap[] imageId;
    public CustomListInvite(Activity context,
                      String[] web, Bitmap[] imageId) {
        super(context, R.layout.list_single_invite, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single_invite, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
        final int rowPosition = position;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                    Data.selectedUsers.add(Data.invitableUsers.get(rowPosition));
                else{
                    //If they are on the list of people to invite, remove them
                    if(Data.selectedUsers.contains(Data.invitableUsers.get(rowPosition)))
                        Data.selectedUsers.remove(Data.invitableUsers.get(rowPosition));
                }
            }
        });
        txtTitle.setText(web[position]);
        imageView.setImageBitmap(imageId[position]);

        return rowView;
    }
}
