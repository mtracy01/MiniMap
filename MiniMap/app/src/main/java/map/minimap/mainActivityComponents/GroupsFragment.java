package map.minimap.mainActivityComponents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.util.ArrayList;

import map.minimap.R;
import map.minimap.helperClasses.FacebookHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String LOG_TAG="GroupsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<String> OptionsList;                //The list of games we have available for users
    private static ListView OptionsListView;    //The Actual UI element id for our games list
    private Context context;

    //Facebook communication protocols
    //private facebookHelper facebook;
    private UiLifecycleHelper uiHelper;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupsFragment.
     */
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Attempt to get our active session
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            Session.setActiveSession((Session) extras.getSerializable("fb_session"));
            Log.e(LOG_TAG,"SUCCESS");
        }
        else{
            Log.e(LOG_TAG,"FAILURE");
        }

        //UI helper for facebook interactions
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        //List of options for this fragment
        OptionsList = new ArrayList<String>();
        OptionsList.add("Friends who use the app");
        OptionsList.add("Invite friends to the app");
        OptionsList.add("My Groups");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        /* Create reaction interfaces for the game buttons in our list */
        OptionsListView = (ListView)view.findViewById(R.id.listView);
        OptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView <?> a, View v, int position,
                                    long id) {
                FacebookHelper helper = new FacebookHelper();
                if(position==0){
                    //List friends who use the app
                    //helper.listFriends(context);
                    new Request(
                            Session.getActiveSession(),
                            "/me/apprequests",
                            null,
                            HttpMethod.GET,
                            new Request.Callback() {
                                public void onCompleted(Response response) {
                                    if (response.getRawResponse().contains("Nicki")) {


                                        Toast.makeText(context.getApplicationContext(),
                                                "Requests are there",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                    ).executeAsync();

                }
                if(position==1){
                    //Invite friends to use the app
                    helper.inviteFriends(context);
                }
                if(position==2){
                    //facebookHelper.listFriends(context);
                    //List user created groups and give them the option to create a new group.
                    //Probably should be implemented in a new activity or heavily embedded into
                    //this one.
                }
            }
        });
        String[] GamesArray = OptionsList.toArray(new String[3]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,GamesArray);
        OptionsListView.setAdapter(adapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Facebook Interaction Methods go here
     */

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(LOG_TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(LOG_TAG, "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
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
