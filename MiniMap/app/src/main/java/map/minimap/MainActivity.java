package map.minimap;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import bolts.AppLinks;
import map.minimap.frameworks.GPSThread;
import map.minimap.frameworks.ServerConnection;
import map.minimap.frameworks.User;
import map.minimap.helperClasses.Data;

import map.minimap.helperClasses.FacebookHelper;
import map.minimap.mainActivityComponents.FriendStatus;
import map.minimap.mainActivityComponents.GamesFragment;
import map.minimap.mainActivityComponents.GroupsFragment;
import map.minimap.mainActivityComponents.InvitationsFragment;
import map.minimap.mainActivityComponents.NavigationDrawerFragment;
import map.minimap.mainActivityComponents.SettingsFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, GamesFragment.OnFragmentInteractionListener, FriendStatus.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener, InvitationsFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener  {

    public void onFragmentInteraction(Uri uri){

    }

    //Debug tag for log console
    private String LOG_TAG="MainActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }
        MediaPlayer mMediaPlayer;
        mMediaPlayer = MediaPlayer.create(this,R.raw.hojus);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.start();
        Log.v("startMiniMap", "Starting MainActivity");
        Data.loggedInFlag=1;
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

        //Link to XML fragments
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.title_section1);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        Data.mainAct = this;




    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(position) {
            case 0:
                fragmentManager.beginTransaction()
                    .replace(R.id.container, GamesFragment.newInstance("a","b"))
                    .commit();
                    break;
            case 1:
                fragmentManager.beginTransaction()
                    .replace(R.id.container, GroupsFragment.newInstance("a","b"))
                    .commit();
                    break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FriendStatus.newInstance("a","b"))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                    .replace(R.id.container, InvitationsFragment.newInstance("a", "b"))
                    .commit();
                    break;
            case 4:
                fragmentManager.beginTransaction()
                    .replace(R.id.container, SettingsFragment.newInstance("a", "b"))
                    .commit();
                    break;
        }
        onSectionAttached(position+1);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = "Settings";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.set
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
