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
