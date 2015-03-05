package map.minimap.frameworks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONObject;

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

    //Invitations handling
    private String requestId;


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
                    if (user !=null) {
                        /* Add user info to a User class that can be stored elsewhere here*/
                        /* For debug purposes, try to print out the desired user info in LogCat)*/
                        try {
                            /* Create new user class here */
                            Log.i(LOG_TAG, user.getId());
                            Log.i(LOG_TAG, user.getName());
                            ourUser = new User(user.getId());
                            ourUser.setName(user.getName());

                            /* Put user in our Data class */
                            Data.user=ourUser;
                            Log.v("loginsetname", Data.user.getName());
                            synchronized (Data.LOGIN_LOCK) {
                                if (Data.client == null || !Data.client.isConnected()) {
                                    Log.v("client", "Starting Client");
                                    ServerConnection client = new ServerConnection( Data.user.getID());
                                    Data.client = client;
                                    client.start();
                                }
                            }
                            //data.setSession(session2);

                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Problem fetching Facebook data!");
                        }

                    }
                }
            });
            if(requestId!=null){
                getRequestData(requestId);
            }
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
        if(session !=null && Session.getActiveSession().isOpened()){
            Intent i = new Intent(getActivity(),MainActivity.class);
            i.putExtra("facebook_session",session);
            startActivity(i);
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
                Log.i(LOG_TAG,"TRUE");
            else
                Log.e(LOG_TAG,"FALSE");
            i.putExtra("facebook_session",session);
            Data.session=session;
            startActivity(i);
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check for an incoming notification. Save the info
        Uri intentUri = getActivity().getIntent().getData();
        if (intentUri != null) {
            String requestIdParam = intentUri.getQueryParameter("request_ids");
            if (requestIdParam != null) {
                String array[] = requestIdParam.split(",");
                requestId = array[0];
                getRequestData(requestId);
                Log.i(LOG_TAG, "Request id: "+requestId);
            }
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

    //Get Request information
    private void getRequestData(final String inRequestId) {
        // Create a new request for an HTTP GET with the
        // request ID as the Graph path.
        Request request = new Request(Session.getActiveSession(),
                inRequestId, null, HttpMethod.GET, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                // Process the returned response
                GraphObject graphObject = response.getGraphObject();
                FacebookRequestError error = response.getError();
                // Default message
                String message = "Incoming request";
                if (graphObject != null) {
                    // Check if there is extra data
                    if (graphObject.getProperty("data") != null) {
                        try {
                            // Get the data, parse info to get the key/value info
                            JSONObject dataObject =
                                    new JSONObject((String)graphObject.getProperty("data"));
                            // Get the value for the key - badge_of_awesomeness
                            String badge =
                                    dataObject.getString("badge_of_awesomeness");
                            // Get the value for the key - social_karma
                            String karma =
                                    dataObject.getString("social_karma");
                            // Get the sender's name
                            JSONObject fromObject =
                                    (JSONObject) graphObject.getProperty("from");
                            String sender = fromObject.getString("name");
                            String title = sender+" sent you a gift";
                            // Create the text for the alert based on the sender
                            // and the data
                            message = title + "\n\n" +
                                    "Badge: " + badge +
                                    " Karma: " + karma;
                        } catch (org.json.JSONException e) {
                            message = "Error getting request info";
                        }
                    } else if (error != null) {
                        message = "Error getting request info";
                    }
                }
                Toast.makeText(getActivity().getApplicationContext(),
                        message,
                        Toast.LENGTH_LONG).show();
            }
        });
        // Execute the request asynchronously.
        Request.executeBatchAsync(request);
    }
}
