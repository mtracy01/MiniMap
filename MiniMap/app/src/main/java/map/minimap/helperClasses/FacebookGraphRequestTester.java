package map.minimap.helperClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import java.util.List;


/**
 * Created by Matthew on 3/30/2015.
 */
public class FacebookGraphRequestTester extends AsyncTask<Void, Void, Void> {

    //debug tag
    private String LOG_TAG="FGRT";

    @Override
    protected Void doInBackground(Void... params) {
        //Log.v(LOG_TAG,"Params verification:" + params[0]);
        new Request(
                Session.getActiveSession(),
                "/" +  Data.user.getID()+ "/invitable_friends",
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        /* handle the result */

                        if(response==null){
                            Log.e(LOG_TAG,"TRUE NULL RESPONSE");
                        }
                        else
                            Log.v(LOG_TAG,"FALSE NULL RESPONSE");
                        List users = response.getGraphObjectList();
                        if(users==null)
                             Log.i(LOG_TAG, "True");
                        else {
                            GraphUser test = (GraphUser) users.get(0);
                            Log.i(LOG_TAG, test.getName());
                        }
                        Log.e(LOG_TAG, "RAW RESPONSE:" + response.toString());
                    }
                }
        ).executeAndWait();
        return null;
    }
}
