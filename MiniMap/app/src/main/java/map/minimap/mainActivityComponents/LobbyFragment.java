package map.minimap.mainActivityComponents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import map.minimap.R;
import map.minimap.frameworks.User;
import map.minimap.frameworks.customUIResources.CustomList;
import map.minimap.frameworks.customUIResources.CustomListInvite;
import map.minimap.helperClasses.Data;

/**
 * Created by Corey on 2/22/2015.
 */
public class LobbyFragment extends android.support.v4.app.Fragment {

    private String LOG_TAG = "LobbyFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static CustomList adapter = null;
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
        if(Data.host == false){
            startButton.setVisibility(View.GONE);
            inviteButton.setVisibility(View.GONE);
        }
        inviteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Data.invitableUsers.clear();
                Data.selectedUsers.clear();
                Data.client.getAllUsers();

                //Do nothing while the client gets invitable users and organizes them into list
                while(Data.clientDoneFlag==0){}

                //when done, reset the client done flag and perform the dialog task
                Data.clientDoneFlag=0;

                Log.v(LOG_TAG,"InvitableUsersSize: " + Data.invitableUsers.size());

                //If we have friends who are online, show the invite dialog
                if(Data.invitableUsers.size()!=0) {
                    String[] playersArray = new String[Data.invitableUsers.size()];
                    Bitmap[] playersPics = new Bitmap[Data.invitableUsers.size()];
                    for (int i = 0; i < Data.invitableUsers.size(); i++) {
                        playersArray[i] = Data.invitableUsers.get(i).getName();
                        playersPics[i] = Data.invitableUsers.get(i).getProfilePhoto();
                    }
                    CustomListInvite inviteAdapter = new CustomListInvite(getActivity(), playersArray, playersPics);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Invite Online Friends");
                    builder.setAdapter(inviteAdapter, null);
                    builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("invite ");
                            builder.append(Data.gameId);
                            for (User u : Data.selectedUsers) {
                                builder.append(' ');
                                builder.append(u.getID());
                            }
                            Data.client.sendMessage(builder.toString());
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                //If we have no invitable friends online, just display a toast that says we have no invitable friends online
                else{
                   Toast.makeText(context,"Sorry, none of your friends are currently online.",Toast.LENGTH_LONG).show();
                }

            }
        });
        //Check if there is a null player, if so, remove that player
        for(int i=0;i<Data.players.size();i++){
            if(Data.players.get(i).getName().equals("")){
                Log.e(LOG_TAG,"True null name");
                Data.players.remove(i);
            }
        }

        Data.lobbyUsers = new ArrayList<String>();
        Bitmap[] playersPics  = new Bitmap[Data.players.size()];
        for(int i =0; i < Data.players.size();i++) {
            Data.lobbyUsers.add(Data.players.get(i).getName());
            playersPics[i] = Data.players.get(i).getProfilePhoto();
            if(playersPics[i]==null) {
                playersPics[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.com_facebook_profile_picture_blank_portrait);
            }
        }
        adapter = new CustomList(getActivity(),Data.lobbyUsers.toArray(new String[Data.players.size()]),playersPics);
        playerListView.setAdapter(adapter);

        return view;
    }
    public static void changeGrid(){

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
