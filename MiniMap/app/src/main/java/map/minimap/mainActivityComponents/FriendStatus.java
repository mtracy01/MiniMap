package map.minimap.mainActivityComponents;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import map.minimap.R;
import map.minimap.frameworks.User;
import map.minimap.frameworks.customUIResources.CustomListStatus;
import map.minimap.helperClasses.Data;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendStatus.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendStatus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendStatus extends  android.support.v4.app.Fragment {

    private ListView friendsListView;
    private Button   refreshButton;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * Purpose: Create a view that shows users their friends and their status of being online or offline in the app
     * @return A new instance of fragment FriendStatus.
     */

    public static FriendStatus newInstance(String a, String b) {
        FriendStatus fragment = new FriendStatus();
        return fragment;
    }

    public FriendStatus() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void refresh(){
        //Set up arrays for inserting into custom adapter
        ArrayList<User> friends = Data.user.getFriends();
        int len= friends.size();
        String[]  names    = new String[len];
        Bitmap[]  pictures = new Bitmap[len];
        boolean[] isOnline = new boolean[len];

        //Get the intersect of users online with users not online into user friends list.  The intersect is stored in invitable friends
        Data.invitableUsers.clear();
        Data.client.getAllUsers();
        while(Data.clientDoneFlag==0) {}
        Data.clientDoneFlag=0;

        //Add necessary information to array needed for our status adapter
        for(int i=0; i<len;i++){
            names[i]=friends.get(i).getName();
            pictures[i]=friends.get(i).getProfilePhoto();
            if(Data.invitableUsers.size()!=0){
                for(int j=0;j<Data.invitableUsers.size();j++){
                    if(Data.invitableUsers.get(j).getID().equals(friends.get(i).getID())) {
                        isOnline[i] = true;
                    }
                }
            }
        }
        CustomListStatus adapter = new CustomListStatus(getActivity(),names,pictures,isOnline);
        friendsListView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Set up our UI elements
        View view = inflater.inflate(R.layout.fragment_friend_status, container, false);
        friendsListView= (ListView)view.findViewById(R.id.friendListView);
        refreshButton = (Button)view.findViewById(R.id.refreshButton);

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
