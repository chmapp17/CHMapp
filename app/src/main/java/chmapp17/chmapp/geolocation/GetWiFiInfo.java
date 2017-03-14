package chmapp17.chmapp.geolocation;

import android.net.wifi.ScanResult;
import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import chmapp17.chmapp.MainActivity;

public class GetWiFiInfo {

    private JSONArray accessPoints;
    private List<ScanResult> networkList;

    public JSONArray getAccessPointObjects(FragmentActivity activity) {

        networkList = MainActivity.networkList;
        accessPoints = new JSONArray();
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
        return accessPoints;
    }
}