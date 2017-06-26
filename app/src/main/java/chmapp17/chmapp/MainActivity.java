package chmapp17.chmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chmapp17.chmapp.database.CrimeInfo;
import chmapp17.chmapp.database.CrimeReview;
import chmapp17.chmapp.database.DataBaseHandling;
import chmapp17.chmapp.database.UsersInfo;

public class MainActivity extends AppCompatActivity {

    private Fragment fragmentHome;
    private Fragment fragmentView;
    private Fragment fragmentAdd;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private WifiManager wifiManager;
    private ScheduledExecutorService scheduleWiFiScan;
    private FirebaseAuth auth;
    private DataBaseHandling dbHandling;
    public static List<ScanResult> networkList;
    public static ArrayList<CrimeInfo> crimeList = new ArrayList<>();
    public static HashMap<Integer, String> mapCrimesKeys = new HashMap<>();
    public static HashMap<String, Integer> mapKeysCrimes = new HashMap<>();
    public static ArrayList<UsersInfo> userList = new ArrayList<>();
    public static ArrayList<CrimeReview> reviewList = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (fragmentHome == null)
                        fragmentHome = new HomeFragment();//new SignupActivity();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragmentHome, "home").commit();
                    break;
                case R.id.navigation_viewcrimes:
                    if (fragmentView == null)
                        fragmentView = new ViewCrimesFragment();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragmentView, "viewc").commit();
                    break;
                case R.id.navigation_addcrime:
                    if (auth.getCurrentUser() != null) {
                        if (fragmentAdd == null)
                            fragmentAdd = new AddCrimeFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content, fragmentAdd, "addc").commit();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please login first to add crimes",
                                Toast.LENGTH_SHORT).show();
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

        fragmentHome = new HomeFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragmentHome, "home").commit();

        auth = FirebaseAuth.getInstance();
        dbHandling = new DataBaseHandling();
        dbHandling.readData();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MainActivity.networkList = wifiManager.getScanResults();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onStart() {
        super.onStart();
        scheduleWiFiScan = Executors.newSingleThreadScheduledExecutor();
        scheduleWiFiScan.scheduleAtFixedRate(scanWiFiNetworks, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scheduleWiFiScan != null)
            scheduleWiFiScan.shutdown();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected Runnable scanWiFiNetworks = new Runnable() {
        Handler handler = new Handler();

        @Override
        public void run() {
            if (!wifiManager.isWifiEnabled()) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Enabling WiFi",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                wifiManager.setWifiEnabled(true);
                while (!wifiManager.isWifiEnabled()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            wifiManager.startScan();
        }
    };
}
