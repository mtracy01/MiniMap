package map.minimap.helperClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import map.minimap.frameworks.GPSThread;
import map.minimap.frameworks.ServerConnection;
import map.minimap.frameworks.User;

/**
 * Created by Matthew on 4/4/2015.
 */
public class FacebookHelper {

    private static String LOG_TAG= "FacebookHelper";


    //Log out of facebook
    public static void logout(){

        //Logout of Facebook
        Data.loggedInFlag=0;
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();

        //Destroy GPS thread and client (if they exist)
        if(Data.gps!=null)      Data.gps.destroyListener();
        if(Data.client!=null)   Data.client.closeSocket();
        Data.client = null;
        Toast.makeText(Data.mainAct.getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
    }

    //returns a list of JSON objects containing the friendsList of a user and relevant information
    public static void getFriendsList(){
        //Run facebook graph request
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
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        User friend = new User(jsonArray.getJSONObject(i).getString("id"));
                        friend.setName(jsonArray.getJSONObject(i).getString("name"));
                        friends.add(friend);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSON Exception when trying to convert friends to users!");
                    }
                }

                //Set friends of the global user object
                Data.user.setFriends(friends);

                //Create a task to get the profile photos of those friends
                AsyncTask<Void,Void,Void> addPhotos = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        ArrayList<User> ourFriends = Data.user.getFriends();
                        for(int i=0;i<ourFriends.size();i++)
                            ourFriends.get(i).setProfilePhoto(getFacebookProfilePicture(ourFriends.get(i).getID()));
                        Data.user.setFriends(ourFriends);
                        Data.user.setProfilePhoto(getFacebookProfilePicture(Data.user.getID()));
                        return null;
                    }
                };
                addPhotos.execute();
            }
        });
        graphRequest.executeAsync();
    }

    public static String getFacebookName(String userID){
        String name = null;
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), userID, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                return;
            }
        });
        GraphResponse graphResponse = graphRequest.executeAndWait();
        if(graphResponse==null){
            Log.e(LOG_TAG,"Failed to get info from ID");
            return null;
        }
        try {
            JSONObject jsonObject = graphResponse.getJSONObject();
            name = jsonObject.getString("name");
        } catch(Exception e){
            Log.e(LOG_TAG,"Exception!");
        }

        return name;
    }


    //Get facebook profile picture and return it
    public static Bitmap getFacebookProfilePicture(String userID){
        Bitmap bitmap = null;
        try {
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=small");
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch( IOException e){
            Log.e(LOG_TAG,"IOException when attempting to retrieve profile pictures!");
        }
        return bitmap;
    }

    public static void appInitializer(){

        //Create client if one is not already created
        if(Data.client==null) {
            GraphRequest.GraphJSONObjectCallback userData = new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                    //Add user data into our user object here
                    String rawResponse = jsonObject.toString();
                    Log.v(LOG_TAG, "Raw Response from Request:" + rawResponse);
                    try {
                        Data.user = new User(jsonObject.getString("id"));
                        Data.user.setName(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }

                    //Starting client (We need to delay this action a little somehow)
                    if (Data.client == null) {
                        Log.v("client", "Starting Client");
                        ServerConnection client = new ServerConnection(Data.user.getID());
                        Data.client = client;
                        client.start();
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Data.gps = new GPSThread(Data.client);
                    }
                    FacebookHelper.getFriendsList();
                }
            };
            GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), userData);
            graphRequest.executeAsync();
        }
    }
}
