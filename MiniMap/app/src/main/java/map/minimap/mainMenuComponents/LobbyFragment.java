package map.minimap.mainMenuComponents;

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
import map.minimap.frameworks.coreResources.IDCipher;
import map.minimap.frameworks.customUIResources.CustomListInvite;
import map.minimap.frameworks.customUIResources.CustomListLobby;
import map.minimap.frameworks.gameResources.User;
import map.minimap.helperClasses.Data;
import map.minimap.helperClasses.GPSHelper;

/**
 * Created by Corey on 2/22/2015.
 */
public class LobbyFragment extends android.support.v4.app.Fragment {

    private String LOG_TAG = "LobbyFragment";
    public static CustomListLobby adapter = null;
    private static ListView playerListView;
    private Context context;

    private OnFragmentInteractionListener mListener;

    public static LobbyFragment newInstance() {
        LobbyFragment fragment = new LobbyFragment();
        return fragment;
    }

    public LobbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //If there is no GPS thread running, start one
        GPSHelper.startGPSThread();

        Log.v("name", Data.user.getName());
        //check server for other players
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 60 * 1000);
            }
        }, 15 * 1000);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_grouplobby, container, false);
        context = getActivity();
        playerListView = (ListView) view.findViewById(R.id.listView);

        final Button startButton = (Button) view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Data.client.startGame();
            }
        });

        final Button inviteButton = (Button) view.findViewById(R.id.inviteButton);
        if (Data.host == false) {
            startButton.setVisibility(View.GONE);
            inviteButton.setVisibility(View.GONE);
        }
        inviteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Data.invitableUsers.clear();
                Data.selectedUsers.clear();
                Data.client.getAllUsers();

                //Do nothing while the client gets invitable users and organizes them into list
                while (Data.clientDoneFlag == 0) {
                }

                //when done, reset the client done flag and perform the dialog task
                Data.clientDoneFlag = 0;

                Log.v(LOG_TAG, "InvitableUsersSize: " + Data.invitableUsers.size());

                //If we have friends who are online, show the invite dialog
                if (Data.invitableUsers.size() != 0) {
                    Log.i(LOG_TAG, "There are " + Data.invitableUsers.size() + " friends online");
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
                                builder.append(IDCipher.toCipher(u.getID()));
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
                else {
                    Log.v(LOG_TAG, "No friends online");
                    Toast.makeText(context, "Sorry, none of your friends are currently online.", Toast.LENGTH_LONG).show();
                }

            }
        });
        //Check if there is a null player, if so, remove that player
        for (int i = 0; i < Data.players.size(); i++) {
            if (Data.players.get(i).getName().equals("")) {
                Log.e(LOG_TAG, "True null name");
                Data.players.remove(i);
            }
        }

        Data.lobbyUsers = new ArrayList<>();
        Bitmap[] playersPics = new Bitmap[Data.players.size()];
        for (int i = 0; i < Data.players.size(); i++) {
            Data.lobbyUsers.add(Data.players.get(i).getName());
            playersPics[i] = Data.players.get(i).getProfilePhoto();
            if (playersPics[i] == null) {
                playersPics[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.com_facebook_profile_picture_blank_portrait);
            }
        }
        adapter = new CustomListLobby(getActivity(), Data.lobbyUsers.toArray(new String[Data.players.size()]), playersPics);
        playerListView.setAdapter(adapter);

        return view;
    }

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
        public void onFragmentInteraction(Uri uri);
    }

}
