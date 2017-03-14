package chmapp17.chmapp;

import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GeoLocation {

    private final int LAT = 0;
    private final int LONG = 1;
    private String url_google_api = "https://www.googleapis.com/geolocation/v1/geolocate?key=";

    public double[] GetLocation(FragmentActivity activity) {
        url_google_api += activity.getString(R.string.google_maps_key);
        double[] param_loc = new double[2];

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
            param_loc[LAT] = location.getDouble("lat");
            param_loc[LONG] = location.getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param_loc;
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
