package map.minimap.mainActivityComponents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import map.minimap.R;
import map.minimap.games.CTFscrimmage;
import map.minimap.helperClasses.Data;


//The games menu
public class GamesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean ctfflag;


    private ArrayList<String> GamesList;                //The list of games we have available for players
    private static ListView GamesListView;    //The Actual UI element id for our games list
    private Context context;
    private OnFragmentInteractionListener mListener;

    public static GamesFragment newInstance(String param1, String param2) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //required empty public constructor
    public GamesFragment() {}

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
        GamesList.add("Assassins");
        GamesList.add("Sardines");
        GamesList.add("Capture the Flag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        /* Create reaction interfaces for the game buttons in our list */
        context =getActivity();
        GamesListView = (ListView)view.findViewById(R.id.listView);
        ctfflag = false;
        GamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView <?> a, View v, int position,
                                    long id) {
                ctfflag = false;
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
                    case 3:
                        ctfflag=true;
                        Data.client.createGameMessage("ctf");
                        break;
                }

                // Add ourselves to the list of players
                Data.players.clear();
                Data.players.add(Data.user);
                Data.host = true;
                if(ctfflag){
                    Intent intent = new Intent(getActivity(), CTFscrimmage.class);
                    startActivity(intent);
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, LobbyFragment.newInstance("a", "b")).setCustomAnimations(R.anim.abc_slide_in_bottom,R.anim.abc_slide_out_bottom).commit();
                }
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
