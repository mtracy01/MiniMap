package map.minimap.helperClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;

/**
 * Purpose: Interact with facebook API to easily request information
 * Created by Matthew on 2/22/2015.
 */
public class facebookHelper {
    private  Context context2;

    public  void inviteFriends(Context context){
        Bundle params = new Bundle();
        params.putString("message", "YOUR_MESSAGE_HERE");
        final Context context1=context;
        context2 = context;
        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(context,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {
                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(context1.getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context1.getApplicationContext(),
                                        "Network Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {
                                Toast.makeText(context1.getApplicationContext(),
                                        "Request sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context1.getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .build();
        requestsDialog.show();
    }

}
