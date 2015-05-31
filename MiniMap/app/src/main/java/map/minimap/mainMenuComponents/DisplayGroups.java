package map.minimap.mainMenuComponents;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import map.minimap.R;
import map.minimap.frameworks.coreResources.IDCipher;
import map.minimap.frameworks.customUIResources.CustomListStatus;
import map.minimap.frameworks.gameResources.User;
import map.minimap.helperClasses.Data;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendStatus.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendStatus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayGroups extends android.support.v4.app.Fragment {

    private ListView friendsListView;
    private Button refreshButton;
    private Button addGroupButton;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * Purpose: Create a view that shows users their friends and their status of being online or offline in the app
     *
     * @return A new instance of fragment FriendStatus.
     */

    public static DisplayGroups newInstance() {
        DisplayGroups fragment = new DisplayGroups();
        return fragment;
    }

    public DisplayGroups() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refresh() {
        //Set up arrays for inserting into custom adapter
        Data.client.sendMessage("getGroupsByID " + IDCipher.toCipher(Data.user.getID()));

        while (Data.clientDoneFlag == 0) {
        }
        Data.clientDoneFlag = 0;

        String groups[] = Data.user.getGroups().split(":");
        Log.v("groupindex", groups[0]);
        if (groups[0].indexOf(',') < 0) {
            return;
        }

        ArrayList<User> users = new ArrayList<User>();
        ArrayList<Integer> nameOffsets = new ArrayList<Integer>();
        ArrayList<String> groupHeaders = new ArrayList<String>();

        int offset = 0;
        int headers = 0;
        for (int i = 0; i < groups.length; i++) {
            String grp[] = groups[i].split(",");
            nameOffsets.add(offset++);
            groupHeaders.add(grp[0]);
            for (int j = 1; j < grp.length; j++) {
                users.add(new User(grp[j]));
                offset++;
            }
        }
        headers = nameOffsets.size();

        for (User u : users) {
            while (u.getProfilePhoto() == null) {
            }
        }

        int len = users.size();
        String[] names = new String[len + headers];
        Bitmap[] pictures = new Bitmap[len + headers];
        boolean[] isOnline = new boolean[len + headers];

        //Get the intersect of users online with users not online into user friends list.  The intersect is stored in invitable friends
        Data.invitableUsers.clear();
        Data.client.getAllUsers();
        while (Data.clientDoneFlag == 0) {
        }
        Data.clientDoneFlag = 0;

        int headerTrack = 0;
        //Add necessary information to array needed for our status adapter
        for (int i = 0; i < len + headers; i++) {
            if (headerTrack < nameOffsets.size() && i == nameOffsets.get(headerTrack)) {
                names[i] = groupHeaders.get(headerTrack);
                pictures[i] = null;
                isOnline[i] = false;
                headerTrack++;
            } else {
                names[i] = users.get(i - headerTrack).getName();
                pictures[i] = users.get(i - headerTrack).getProfilePhoto();
                if (Data.invitableUsers.size() != 0) {
                    for (int j = 0; j < Data.invitableUsers.size(); j++) {
                        if (Data.invitableUsers.get(j).getID().equals(users.get(i - headerTrack).getID())) {
                            isOnline[i] = true;
                        }
                    }
                }
            }
        }
        CustomListStatus adapter = new CustomListStatus(getActivity(), names, pictures, isOnline);
        friendsListView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Set up our UI elements
        View view = inflater.inflate(R.layout.fragment_friend_status, container, false);
        friendsListView = (ListView) view.findViewById(R.id.friendListView);
        refreshButton = (Button) view.findViewById(R.id.refreshButton);

        view.setBackgroundColor(Color.WHITE);
        refresh();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
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
}
