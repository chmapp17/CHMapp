package chmapp17.chmapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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

    Handler handler = new Handler();
    public static boolean isAppVisible = true;
    public static List<ScanResult> networkList;
    public static final int LOCATION_PERMISSION_REQCODE = 0;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAppVisible = true;
        fragment = new HomeFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment, "home").commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQCODE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAppVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppVisible = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQCODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scheduleWiFiScan = Executors.newSingleThreadScheduledExecutor();
                    scheduleWiFiScan.scheduleAtFixedRate(scanWiFiNetworks, 0, 10, TimeUnit.SECONDS);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected Runnable scanWiFiNetworks = new Runnable() {

        @Override
        public void run() {
            if (isAppVisible) {
                new AsyncTask<Void, Void, List<ScanResult>>() {
                    @Override
                    protected List<ScanResult> doInBackground(Void... voids) {
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
                        registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                wifiManager.getScanResults();
                            }
                        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                        return wifiManager.getScanResults();
                    }

                    @Override
                    protected void onPostExecute(List<ScanResult> networkList) {
                        MainActivity.networkList = networkList;
                    }
                }.execute();
            }
        }
    };
}
