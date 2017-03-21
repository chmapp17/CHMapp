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

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private ScheduledExecutorService scheduleWiFiScan;
    public static List<ScanResult> networkList;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (fragmentManager.findFragmentByTag("home") == null) {
                        fragment = new HomeFragment();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.content, fragment, "home").commit();
                    }
                    break;
                case R.id.navigation_viewcrimes:
                    if (fragmentManager.findFragmentByTag("viewc") == null) {
                        fragment = new ViewCrimesFragment();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.content, fragment, "viewc").commit();
                    }
                    break;
                case R.id.navigation_addcrime:
                    if (fragmentManager.findFragmentByTag("addc") == null) {
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

        fragment = new HomeFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment, "home").commit();

        registerReceiver(new BroadcastReceiver() {
            final WifiManager wifiManager = (WifiManager) getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);

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
            final WifiManager wifiManager = (WifiManager) getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
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
