package GetLocationByCellTower;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import GetLocationByCellTower.GetCellTowerInfo;
import chmapp17.chmapp.R;

public class GetLocationByCELL extends AppCompatActivity {

    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;
    private double _lat = 0,_lng=0;
    private JSONObject JResponse;
    private String url_google_api = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyCXbCzfN-SOfqLoivudw8GbQcR2WdvABF4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_location_by_cell);

    }
    public double [] GetLocation(Context _ctx)
    {
        double [] param_loc = new double[2];
        GetCellTowerInfo  twr = new GetCellTowerInfo();

        JSONArray js = twr.getCellInfo(_ctx);
        SendRequest(js,_ctx);

        try {

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
                //System.out.println("" + sb.toString());
                result = sb.toString();
            }

            con.disconnect();
            //return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    private void SendRequest(JSONArray js, Context _ctx) {

        try {
            final JSONObject toPost = new JSONObject();
            toPost.put("cellTowers", js);
            String msg = getServerResponse(toPost);
            JResponse = new JSONObject(msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


