package map.minimap;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import map.minimap.mainActivityComponents.FriendStatus;
import map.minimap.mainActivityComponents.GamesFragment;
import map.minimap.mainActivityComponents.GroupsFragment;
import map.minimap.mainActivityComponents.InvitationsFragment;
import map.minimap.mainActivityComponents.NavigationDrawerFragment;
import map.minimap.mainActivityComponents.SettingsFragment;
import map.minimap.mainMenuComponents.ContentFragment;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class MainMenu extends ActionBarActivity
        implements GamesFragment.OnFragmentInteractionListener, FriendStatus.OnFragmentInteractionListener, GroupsFragment.OnFragmentInteractionListener, InvitationsFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener,
         ViewAnimator.ViewAnimatorListener{

    private int res = R.drawable.abc_item_background_holo_light;
    private Fragment fragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ContentFragment contentFragment;
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;

    /**
     * Used to store the last screen title.
     */
    private CharSequence mTitle;

    //Logging variables
    private String LOG_TAG = "MainMenu";
    //private AppEventsLogger logger = AppEventsLogger.newLogger(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        contentFragment = ContentFragment.newInstance(R.drawable.abc_item_background_holo_light);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, GamesFragment.newInstance("a","b"))
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

    }

    private void setActionBar() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setNavigationIcon(R.drawable.sat_main);

        setSupportActionBar(toolbar);
        //getSupportActionBar().set
        toolbar.setNavigationIcon(R.drawable.ic_launcher);
       // toolbar.setLogo(R.drawable.minimaplogo);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.ic_launcher);
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
    public void onFragmentInteraction(Uri uri){

    }


    //Methods specialized for new menu here
    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.ic_launcher);
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(ContentFragment.GAMES, R.drawable.ic_launcher);
        list.add(menuItem);
        SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.GROUPS, R.drawable.ic_launcher);
        list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(ContentFragment.FRIENDS, R.drawable.ic_launcher);
        list.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.SETTINGS, R.drawable.ic_launcher);
        list.add(menuItem4);
    }

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition, String name) {
        //this.res = this.res == R.drawable.com_facebook_tooltip_blue_xout ? R.drawable.com_facebook_button_icon : R.drawable.powered_by_google_light;
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();
        //ContentFragment contentFragment = ContentFragment.newInstance(this.res);

        //Switch depending on the name of the Menu
        switch(name){
            case ContentFragment.GAMES:
                Log.v(LOG_TAG, "Attempting switch to games");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,GamesFragment.newInstance("a","b")).commit();
                //We do nothing, so close
                break;
            case ContentFragment.GROUPS:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,GroupsFragment.newInstance("a","b")).commit();
                break;
            case ContentFragment.FRIENDS:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,FriendStatus.newInstance("a","b")).commit();
                break;
            case ContentFragment.SETTINGS:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,SettingsFragment.newInstance("a","b")).commit();
                break;
            case "Logout":
                //Logout?
                break;
        }

        return contentFragment;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        Log.v(LOG_TAG,""+position);
        switch (slideMenuItem.getName()) {
            case ContentFragment.CLOSE:
                return screenShotable;
            default:
                Log.v(LOG_TAG,"Name = " + slideMenuItem.getName());
                return replaceFragment(screenShotable, position, slideMenuItem.getName());
        }
        //return replaceFragment(screenShotable,position);
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