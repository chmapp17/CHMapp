package chmapp17.chmapp.geolocation;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import chmapp17.chmapp.R;

public class GeoLocation {

    private LatLng latLng;
    private double accuracy;
    private String url_google_api = "https://www.googleapis.com/geolocation/v1/geolocate?key=";

    public LatLng GetLocation(FragmentActivity activity) {

        url_google_api += activity.getString(R.string.google_maps_key);

        GetCellTowerInfo cellTowerInfo = new GetCellTowerInfo();
        JSONArray CTobj = cellTowerInfo.getCellTowerObjects(activity);
        GetWiFiInfo wiFiInfo = new GetWiFiInfo();
        JSONArray APobj = wiFiInfo.getAccessPointObjects(activity);
        final JSONObject toPost = new JSONObject();

        try {
            toPost.put("cellTowers", CTobj);
            toPost.put("wifiAccessPoints", APobj);
            String msg = getServerResponse(toPost);
            JSONObject JResponse = new JSONObject(msg);
            JSONObject location = JResponse.getJSONObject("location");
            latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
            accuracy = JResponse.getDouble("accuracy");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    public double GetAccuracy(){
        return accuracy;
    }

    private String getServerResponse(JSONObject toPost) {

        String result = "";
        try {
            URL object = new URL(url_google_api);
            HttpURLConnection conn = (HttpURLConnection) object.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(toPost.toString());
            osw.flush();

            StringBuilder sb = new StringBuilder();
            int HttpResult = conn.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                result = sb.toString();
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
