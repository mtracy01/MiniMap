package map.minimap;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;


public class LoginActivity extends FragmentActivity {

    //LoginTag
    private String LOG_TAG = "LoginActivity";


    private int loggedInFlag=0;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.v(LOG_TAG,"SUCCESSful:D");

                        loggedInFlag=1;
                        result=loginResult;
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        //handlePendingAction();
                        //updateUI();
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
            Data.
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
