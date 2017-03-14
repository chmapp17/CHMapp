package chmapp17.chmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetWiFiInfo {

    private JSONArray accessPoints;
    private List<ScanResult> networkList;
    private WifiManager wifiManager;

    public JSONArray getAccessPointObjects(FragmentActivity activity) {

        accessPoints = new JSONArray();
        wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        activity.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                networkList = wifiManager.getScanResults();
                if (networkList != null) {
                    try {
                        JSONObject apObj = new JSONObject();
                        for (ScanResult network : networkList) {
                            apObj.put("macAddress", network.BSSID);
                            apObj.put("signalStrength", network.level);
                            apObj.put("age", network.timestamp / 1000);
                            accessPoints.put(apObj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        return accessPoints;
    }
}