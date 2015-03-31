package map.minimap;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import map.minimap.helperClasses.Data;

import map.minimap.mainActivityComponents.GamesFragment;
import map.minimap.mainActivityComponents.GroupsFragment;
import map.minimap.mainActivityComponents.InvitationsFragment;
import map.minimap.mainActivityComponents.NavigationDrawerFragment;
import map.minimap.mainActivityComponents.SettingsFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, GamesFragment.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener, InvitationsFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener  {

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
        setContentView(R.layout.activity_main);
        Log.v("startMiniMap", "startMiniMap");
        //set default userID in case something goes wrong

        //Get our variables from LoginActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Session.setActiveSession((Session) extras.getSerializable("fb_session"));

            Log.e(LOG_TAG,"SUCCESS");
        }
        else{
            Log.e(LOG_TAG, "FAILURE");
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
                    .replace(R.id.container, InvitationsFragment.newInstance("a", "b"))
                    .commit();
                    break;
            case 3:
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
