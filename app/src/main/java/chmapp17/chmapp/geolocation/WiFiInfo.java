package chmapp17.chmapp.geolocation;

import android.net.wifi.ScanResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import chmapp17.chmapp.MainActivity;

public class WiFiInfo {

    public JSONArray getAccessPointObjects() {

        List<ScanResult> networkList = MainActivity.networkList;
        JSONArray accessPoints = new JSONArray();
        if (networkList != null) {
            try {
                for (ScanResult network : networkList) {
                    JSONObject apObj = new JSONObject();
                    apObj.put("macAddress", network.BSSID);
                    apObj.put("signalStrength", network.level);
                    accessPoints.put(apObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return accessPoints;
    }
}
