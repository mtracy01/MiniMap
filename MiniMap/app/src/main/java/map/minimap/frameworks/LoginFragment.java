package map.minimap.frameworks;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import map.minimap.MainActivity;
import map.minimap.R;
import map.minimap.helperClasses.Data;

/**
 * Created by Matthew on 2/17/2015.
 */
public class LoginFragment extends android.support.v4.app.Fragment {

    //Debug log_tag for debug console
    private String LOG_TAG="LoginFragment";

    private UiLifecycleHelper uiHelper;

    private User ourUser;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile","user_friends","email"));
        authButton.setFragment(this);

        return view;
    }

    //Facebook login handler
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(LOG_TAG, "Logged in...");
            final Session session2=session;
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        /* Add user info to a User class that can be stored elsewhere here*/
                        /* For debug purposes, try to print out the desired user info in LogCat)*/
                        try {
                            /* Create new user class here */
                            Log.e(LOG_TAG, user.getId());
                            Log.e(LOG_TAG, user.getName());
                            ourUser = new User(user.getId());
                            ourUser.setName(user.getName());

                            /* Put user in our Data class */
                            Data.user=ourUser;
                            //data.setSession(session2);

                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Problem fetching Facebook data!");
                        }

                    }
                }
            });
        } else if (state.isClosed()) {
            Log.i(LOG_TAG, "Logged out...");
        }
    }

    //Facebook Login Callback listener
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

        /* If we are successful, go to our MainActivity */
        if (Session.getActiveSession() != null || Session.getActiveSession().isOpened()){
            Intent i = new Intent(getActivity(),MainActivity.class);
            Session session = Session.getActiveSession();
            if(session.isOpened())
                Log.e(LOG_TAG,"TRUE");
            else
                Log.e(LOG_TAG,"False");
            i.putExtra("facebook_session",session);
            startActivity(i);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

}
