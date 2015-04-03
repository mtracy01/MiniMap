package map.minimap.mainActivityComponents;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import map.minimap.R;
import map.minimap.frameworks.User;
import map.minimap.helperClasses.Data;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GamesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesFragment extends android.support.v4.app.Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ArrayList<String> GamesList;                //The list of games we have available for users
    private static ListView GamesListView;    //The Actual UI element id for our games list
    private Context context;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GamesFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static GamesFragment newInstance(String param1, String param2) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GamesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        /* Add Application Names here as they are ready for testing to create their button in the UI */
        GamesList = new ArrayList<>();
        GamesList.add("Friend Finder");
        //GamesList.add("Capture the Flag");
        GamesList.add("Assassins");
        GamesList.add("Sardines");
        //GamesList.add("Slender");




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_games, container, false);
        /* Create reaction interfaces for the game buttons in our list */
        context =getActivity();
        GamesListView = (ListView)view.findViewById(R.id.listView);
        GamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView <?> a, View v, int position,
                                    long id) {
                Log.v("id", Data.client.toString());
                switch(position){
                    case 0:
                        Data.client.createGameMessage("friendFinder");
                        break;
                    case 1:
                        Data.client.createGameMessage("assassins");
                        break;
                    case 2:
                        Data.client.createGameMessage("sardines");
                        break;
                }

                // Add ourselves to the list of users
                if (Data.users == null) {
                    Data.users = new ArrayList<User>();
                } else {
                    Data.users.clear();
                }
                Data.users.add(Data.user);

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, LobbyFragment.newInstance("a","b"));
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        String[] GamesArray = GamesList.toArray(new String[3]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,GamesArray);
        GamesListView.setAdapter(adapter);
        return view;
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
        // TODO: Update argument type and name

        public void onFragmentInteraction(Uri uri);
    }
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

}
