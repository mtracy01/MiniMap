package map.minimap.frameworks;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import map.minimap.R;

/**
 * Created by Matthew on 2/17/2015.
 */
public class LoginFragment extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        return view;
    }
}
