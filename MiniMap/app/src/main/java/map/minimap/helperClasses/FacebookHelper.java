package map.minimap.helperClasses;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import java.util.List;

/**
 * Created by Matthew on 4/4/2015.
 */
public class FacebookHelper {

    private static String LOG_TAG= "FacebookHelper";


    //Log out of facebook
    public static void logOut(){
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();
    }

    //returns a list of JSON objects containing the friendsList of a user and relevant information
    public static void getFriendsList(){

        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "user_friends", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                if(graphResponse==null){
                    Log.e(LOG_TAG,"getFriendsList got a null graph response!");
                    return;
                }
                Log.v(LOG_TAG,"Raw Response from getFriendsList: " + graphResponse.getRawResponse());
                return;
            }
        });
        graphRequest.executeAsync();
        return;
    }
}
