package map.minimap.mainMenuComponents;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import java.util.ArrayList;

import map.minimap.R;

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

    private ArrayList<String> OptionsList;                //The list of games we have available for players
    private static ListView OptionsListView;    //The Actual UI element id for our games list
    private Context context;

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

        //UI helper for facebook interactions


        //List of options for this fragment
        OptionsList = new ArrayList<>();
        OptionsList.add("Friends who use the app");
        OptionsList.add("Invite friends to the app");
        OptionsList.add("My Groups");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentManager fragmentManager = getFragmentManager();

        context = getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        /* Create reaction interfaces for the game buttons in our list */
        OptionsListView = (ListView)view.findViewById(R.id.listView);
        OptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView <?> a, View v, int position,
                                    long id) {

                if(position==0){
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, FriendStatus.newInstance("a","b"))
                            .commit();
                }
                if(position==1){
                    //Invite friends to use the app
                    //TODO: Get the app on the play store so people can actually download this stuff
                    String appLinkUrl, previewImageUrl;

                    appLinkUrl = "https://fb.me/1637179893181427";
                    previewImageUrl = "https://lh3.ggpht.com/4j_GW-fYL2FaHsTavBHh54IXLCcQDSjr4DDeJI69IKg6SVLXNL47-0zPn7EH3RXTGK7_=h900-rw";

                    if (AppInviteDialog.canShow()) {
                        AppInviteContent content = new AppInviteContent.Builder()
                                .setApplinkUrl(appLinkUrl)
                                .setPreviewImageUrl(previewImageUrl)
                                .build();
                        AppInviteDialog.show(getActivity(), content);
                    }

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,GamesArray);
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
        void onFragmentInteraction(Uri uri);
    }

}
