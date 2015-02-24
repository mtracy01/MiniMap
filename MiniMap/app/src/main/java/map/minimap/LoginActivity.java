package map.minimap;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import map.minimap.frameworks.LoginFragment;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    //Debug variables
    private static final String LOG_TAG = "LoginActivity";

    //Fragment Reference
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            loginFragment = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, loginFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            loginFragment = (LoginFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }
}



