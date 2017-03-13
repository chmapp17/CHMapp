package chmapp17.chmapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(fragmentManager.findFragmentByTag("home") == null) {
                        fragment = new HomeFragment();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.content, fragment, "home").commit();
                    }
                    break;
                case R.id.navigation_viewcrimes:
                    if(fragmentManager.findFragmentByTag("viewc") == null) {
                        fragment = new ViewCrimesFragment();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.content, fragment, "viewc").commit();
                    }
                    break;
                case R.id.navigation_addcrime:
                    if(fragmentManager.findFragmentByTag("addc") == null) {
                        fragment = new AddCrimeFragment();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.content, fragment, "addc").commit();
                    }
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragment = new HomeFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment, "home").commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }
}
