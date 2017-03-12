package GetLocationByCellTower;


import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class GetLocationByCELL{

    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;
    private static final String url_google_api = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyCXbCzfN-SOfqLoivudw8GbQcR2WdvABF4";


    public double [] GetLocation(Context _ctx)
    {
        double [] param_loc = new double[2];
        GetCellTowerInfo  twr = new GetCellTowerInfo();

        JSONArray js = twr.getCellInfo(_ctx);
        final JSONObject toPost = new JSONObject();

        try {
            toPost.put("cellTowers", js);
            String msg = getServerResponse(toPost);
            JSONObject JResponse = new JSONObject(msg);

            JSONObject location = JResponse.getJSONObject("location");
            param_loc[LATITUDE] = location.getDouble("lat");
            param_loc[LONGITUDE] = location.getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param_loc;
    }

    private String getServerResponse(JSONObject toPost) {

        String result = "";
        try {

            URL object = new URL(url_google_api);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(toPost.toString());
            wr.flush();

            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                result = sb.toString();
            }
            con.disconnect();
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


