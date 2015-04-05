package map.minimap.helperClasses;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import map.minimap.frameworks.User;

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
        GraphRequest graphRequest = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                if (graphResponse == null) {
                    Log.e(LOG_TAG, "getFriendsList got a null graph response!");
                    return;
                }
                Log.v(LOG_TAG, "Raw Response from getFriendsList: " + graphResponse.getRawResponse());

                //Parse our friends into a friends list, then set the friends of the global user object
                ArrayList<User> friends = new ArrayList<>();
                for(int i=0; i<jsonArray.length();i++){
                    try {
                        User friend = new User(jsonArray.getJSONObject(i).getString("id"));
                        friend.setName(jsonArray.getJSONObject(i).getString("name"));
                        friends.add(friend);
                    } catch(JSONException e){
                        Log.e(LOG_TAG,"JSON Exception when trying to convert friends to users!");
                    }
                }

                //Set friends of the global user object
                Data.user.setFriends(friends);
            }
        });
        graphRequest.executeAsync();
        //return;
    }
}
