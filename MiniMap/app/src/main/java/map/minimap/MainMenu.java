package map.minimap;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.appevents.AppEventsLogger;

import map.minimap.mainActivityComponents.FriendStatus;
import map.minimap.mainActivityComponents.GamesFragment;
import map.minimap.mainActivityComponents.GroupsFragment;
import map.minimap.mainActivityComponents.InvitationsFragment;
import map.minimap.mainActivityComponents.NavigationDrawerFragment;
import map.minimap.mainActivityComponents.SettingsFragment;


public class MainMenu extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, GamesFragment.OnFragmentInteractionListener, FriendStatus.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener, InvitationsFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener  {

    /**
     * Used to store the last screen title.
     */
    private CharSequence mTitle;

    //Logging variables
    private String LOG_TAG = "MainMenu";
    private AppEventsLogger logger = AppEventsLogger.newLogger(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case 0:
                logger.logEvent("Open Games menu");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, GamesFragment.newInstance("a", "b"))
                        .commit();
                break;
            case 1:
                logger.logEvent("Open Groups menu");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, GroupsFragment.newInstance("a", "b"))
                        .commit();
                break;
            case 2:
                logger.logEvent("Open Friends menu");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FriendStatus.newInstance("a", "b"))
                        .commit();
                break;
            case 3:
                logger.logEvent("Open Invitations menu");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, InvitationsFragment.newInstance("a", "b"))
                        .commit();
                break;
            case 4:
                logger.logEvent("Open Settings menu");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance("a", "b"))
                        .commit();
                break;
        }
        onSectionAttached(position + 1);
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
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }

    }

    public void onFragmentInteraction(Uri uri){

    }
}