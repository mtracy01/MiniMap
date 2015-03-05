package map.minimap.mainActivityComponents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import map.minimap.FriendFinder;
import map.minimap.R;
import map.minimap.helperClasses.Data;
import map.minimap.helperClasses.FacebookHelper;

/**
 * Created by Corey on 2/22/2015.
 */
public class LobbyFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static ArrayAdapter<String> adapter = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static ArrayList<String> playersList;           //The list of players shared with client
    private static ListView playerListView;
    private Context context;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InvitationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LobbyFragment newInstance(String param1, String param2) {
        LobbyFragment fragment = new LobbyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LobbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        playersList = new ArrayList<String>();
        playersList.add(Data.user.getName());
        Log.v("name",Data.user.getName());
        //check server for other players
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
        @Override
        public void run() {

            handler.postDelayed( this, 60 * 1000 );
        }
        }, 15 * 1000 );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_grouplobby, container, false);
        context =getActivity();
        playerListView = (ListView)view.findViewById(R.id.listView);
        final Button startButton = (Button) view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Data.client.startGame();
            }
        });
        final Button inviteButton = (Button) view.findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Data.client.getAllUsers();
              //  FacebookHelper helper = new FacebookHelper();
             //  helper.inviteFriends(view.getContext());

            }
        });
        String[] playersArray = playersList.toArray(new String[1]);
        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,playersArray);
        playerListView.setAdapter(adapter);
        return view;
    }
    public static void changeGrid(){
        adapter.notifyDataSetChanged();
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
      /*  try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
