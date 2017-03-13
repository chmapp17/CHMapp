package chmapp17.chmapp;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetWiFiInfo{

    WifiManager wifiManager;
    private JSONArray accessPoints;

    public JSONArray getAccessPointObjects(Context context){

        accessPoints = new JSONArray();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> networkList = wifiManager.getScanResults();
        if (networkList != null) {
            JSONObject apObj = new JSONObject();
            for (ScanResult network : networkList)
            {
                try {
                    apObj.put("macAddress", network.BSSID);
                    apObj.put("signalStrength", network.level);
                    apObj.put("age", network.timestamp/1000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                accessPoints.put(apObj);
            }
        }
        return accessPoints;
    }
}