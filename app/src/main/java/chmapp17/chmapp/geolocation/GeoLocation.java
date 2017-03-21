package chmapp17.chmapp.geolocation;

import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import chmapp17.chmapp.R;

public class GeoLocation {

    private Location location;
    private String url_google_api = "https://www.googleapis.com/geolocation/v1/geolocate?key=";

    public Location getLocation(Context context) {

        url_google_api += context.getString(R.string.google_maps_key);

        JSONArray CTobj = (new CellTowerInfo()).getCellTowerObjects(context);
        JSONArray APobj = (new WiFiInfo()).getAccessPointObjects();
        final JSONObject toPost = new JSONObject();

        try {
            toPost.put("cellTowers", CTobj);
            toPost.put("wifiAccessPoints", APobj);
            JSONObject JResponse = new JSONObject(getServerResponse(toPost));
            JSONObject locObject = JResponse.getJSONObject("location");
            location = new Location("");
            location.setLatitude(locObject.getDouble("lat"));
            location.setLongitude(locObject.getDouble("lng"));
            location.setAccuracy((float) JResponse.getDouble("accuracy"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
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
