package map.minimap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.List;

import map.minimap.frameworks.coreResources.IDCipher;
import map.minimap.helperClasses.Data;
import map.minimap.helperClasses.FacebookHelper;
import map.minimap.helperClasses.GPSHelper;
import map.minimap.mainMenuComponents.ContentFragment;
import map.minimap.mainMenuComponents.DisplayGroups;
import map.minimap.mainMenuComponents.FriendStatus;
import map.minimap.mainMenuComponents.GamesFragment;
import map.minimap.mainMenuComponents.GroupsFragment;
import map.minimap.mainMenuComponents.LobbyFragment;
import map.minimap.mainMenuComponents.SettingsFragment;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class MainMenu extends ActionBarActivity
        implements GamesFragment.OnFragmentInteractionListener, FriendStatus.OnFragmentInteractionListener, DisplayGroups.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener,
        ViewAnimator.ViewAnimatorListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ContentFragment contentFragment;
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;

    //Logging variables
    private String LOG_TAG = "MainMenu";
    private AppEventsLogger logger = AppEventsLogger.newLogger(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Data.mainAct = this;
        Data.mainContext = this;

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        Data.loggedInFlag = 1;
        FacebookHelper.appInitializer();

        contentFragment = ContentFragment.newInstance(R.drawable.abc_item_background_holo_light);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, GamesFragment.newInstance())
                .commit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);
        if (getIntent().hasExtra("ctf"))
            if ((getIntent().getStringExtra("ctf")).equals("scrim line done")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, LobbyFragment.newInstance()).setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom).commit();
            }
    }

    private void setActionBar() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_accept);
        getSupportActionBar().setTitle("Games");

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.v(LOG_TAG, "Menu Item is: " + menuItem.getItemId());
                if (menuItem.getItemId() == R.id.action_logout) {
                    FacebookHelper.logout();
                    startActivity(new Intent(MainMenu.this, LoginActivity.class));
                    overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_top);
                } else if (menuItem.getItemId() == R.id.invite_friends) {
                    FacebookHelper.inviteFriends();
                }
                return true;
            }
        });
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    public void onFragmentInteraction(Uri uri) {

    }

    //Methods specialized for new menu here
    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.ic_action_remove);
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(ContentFragment.GAMES, R.drawable.ic_action_gamepad);
        list.add(menuItem);
        //SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.GROUPS, R.drawable.ic_action_group);
        //list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(ContentFragment.FRIENDS, R.drawable.ic_action_person);
        list.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.SETTINGS, R.drawable.ic_action_settings);
        list.add(menuItem4);
    }

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, String name) {
        //Switch depending on the name of the Menu
        switch (name) {
            case ContentFragment.GAMES:
                logger.logEvent("Games Menu");
                Log.v(LOG_TAG, "Games Menu");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, GamesFragment.newInstance()).setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom).addToBackStack("Games").commit();
                getSupportActionBar().setTitle("Games");
                break;
            case ContentFragment.GROUPS:
                logger.logEvent("Groups Menu");
                Log.v(LOG_TAG, "Groups Menu");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, GroupsFragment.newInstance()).setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom).commit();
                getSupportActionBar().setTitle("Groups");
                break;
            case ContentFragment.FRIENDS:
                logger.logEvent("Friends Menu");
                Log.v(LOG_TAG, "Friends Menu");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, FriendStatus.newInstance()).setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom).commit();
                getSupportActionBar().setTitle("Friends");
                break;
            case ContentFragment.SETTINGS:
                logger.logEvent("Settings Menu");
                Log.v(LOG_TAG, "Settings Menu");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, SettingsFragment.newInstance()).setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom).commit();
                getSupportActionBar().setTitle("Settings");
                break;
        }
        return contentFragment;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        Log.v(LOG_TAG, "" + position);
        switch (slideMenuItem.getName()) {
            case ContentFragment.CLOSE:
                return screenShotable;
            default:
                Log.v(LOG_TAG, "Name = " + slideMenuItem.getName());
                return replaceFragment(screenShotable, slideMenuItem.getName());
        }
    }

    @Override
    public void onBackPressed() {
        final Context context = this;

        //If we are in a lobby, ask the user if they would like to leave the lobby
        if (getSupportFragmentManager().getBackStackEntryAt(0).getName().equals("lobby")) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit the lobby?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            MainMenu.super.onBackPressed();
                            Data.client.sendMessage("remove " + Data.gameId + " " + IDCipher.toCipher(Data.user.getID()));
                            GPSHelper.killGPSThread();
                            startActivity(new Intent(context, MainMenu.class));
                        }
                    }).create().show();
        } else {
            super.onBackPressed();
            GPSHelper.killGPSThread();
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();
    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}