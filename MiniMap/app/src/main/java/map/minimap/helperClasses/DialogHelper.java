package map.minimap.helperClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import map.minimap.MainMenu;

/**
 * Created by Matthew on 4/15/2015.
 */
public class DialogHelper {
    public static void exitDialog(Context context2){
        final Context context = context2;
        /*new AlertDialog.Builder(context2)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit the game?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        context.getC.super.onBackPressed();
                        Data.client.sendMessage("remove " + Data.gameId + " " + Data.user.getID());
                        startActivity(new Intent(context,MainMenu.class));
                    }
                }).create().show();*/
    }
}
