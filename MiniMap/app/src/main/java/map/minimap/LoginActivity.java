package map.minimap;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import map.minimap.frameworks.GPSThread;
import map.minimap.frameworks.ServerConnection;
import map.minimap.frameworks.User;
import map.minimap.helperClasses.Data;


public class LoginActivity extends FragmentActivity {

    //LoginTag
    private String LOG_TAG = "LoginActivity";

    private CallbackManager callbackManager;
    private PendingAction pendingAction = PendingAction.NONE;
    private final String PENDING_ACTION_BUNDLE_KEY =
            "com.facebook.samples.hellofacebook:PendingAction";
    private ProfileTracker profileTracker;
    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    private LoginResult result=null;

    private int startCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        startCount=0;
        callbackManager = CallbackManager.Factory.create();
        Data.mainAct=getParent();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.v(LOG_TAG, "SUCCESSful:D");

                        result = loginResult;

                        //Graph request to get our user's Facebook data
                        GraphRequest.GraphJSONObjectCallback userData = new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                //Add user data into our user object here
                                String rawResponse = jsonObject.toString();
                                Log.v(LOG_TAG, "Raw Response from Request:" + rawResponse);
                                try {
                                    Data.user = new User(jsonObject.getString("id"));
                                    Data.user.setName(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));
                                } catch(JSONException e){
                                    Log.e(LOG_TAG, e.getMessage());
                                }

                                //Starting client
                                if (startCount ==0) {
                                   Log.v("client", "Starting Client");
                                   ServerConnection client = new ServerConnection(Data.user.getID());
                                   Data.client = client;
                                   client.start();
                                   try {
                                       Thread.sleep(200);
                                   }catch (Exception e) {
                                       e.printStackTrace();
                                   }
                                    if(Data.client !=null){
                                       // Data.gps = new GPSThread(Data.client);
                                    }
                                    //If client fails to be created, log out the user from facebook and do not advance intents to next activity
                                }
                                startCount++;
                            }
                        };
                        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), userData);
                        graphRequest.executeAsync();

                        if(Data.client!=null) {
                            Log.v(LOG_TAG,"Client is not NULL, proceeding to login");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        //We did not communicate successfully, log back out of facebook
                        else{
                            //Display error
                            Log.e(LOG_TAG,"Unable to connect to our server, aborting login");
                            Toast toast = Toast.makeText(getApplicationContext(),"Unable to connect to Server",Toast.LENGTH_SHORT);
                            toast.show();

                            //Logout
                            //LoginManager lm = LoginManager.getInstance();
                            //lm.logOut();
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (pendingAction != PendingAction.NONE) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        Log.v(LOG_TAG,"Cancelled");
                        updateUI();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        Log.v(LOG_TAG,"Error!");
                        updateUI();
                    }

                    private void showAlert() {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle(R.string.cancelled)
                                .setMessage(R.string.permission_not_granted)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                });


        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AccessToken.getCurrentAccessToken()!=null){
            //Data.
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);

        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // profileTracker.stopTracking();
    }

    private void updateUI() {

        //If we are already logged in, go ahead to main activity.
        if(result!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;



        Profile profile = Profile.getCurrentProfile();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
               // postPhoto();
                break;
            case POST_STATUS_UPDATE:
               // postStatusUpdate();
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
