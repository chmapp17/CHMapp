package chmapp17.chmapp.map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import chmapp17.chmapp.MainActivity;
import chmapp17.chmapp.R;
import chmapp17.chmapp.geolocation.GeoLocation;

public class MapHandling {

    public void updateMapPosition(final FragmentActivity activity, final GoogleMap googleMap) {
        if (MainActivity.isNetworkAvailable(activity)) {
            final GeoLocation geoLocation = new GeoLocation();
            new AsyncTask<Void, Void, LatLng>() {
                @Override
                protected LatLng doInBackground(Void... voids) {
                    return geoLocation.GetLocation(activity);
                }

                @Override
                protected void onPostExecute(LatLng latLng) {
                    googleMap.clear();
                    float currZoom = googleMap.getCameraPosition().zoom;
                    if (currZoom == 2) {
                        googleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 15)));
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.fromLatLngZoom(latLng, currZoom)));
                    }
                    googleMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                    googleMap.addCircle(new CircleOptions()
                            .center(latLng).radius(geoLocation.GetAccuracy())
                            .fillColor(Color.argb(30, 0, 155, 255))
                            .strokeColor(Color.argb(255, 0, 155, 255)).strokeWidth(2));
                }
            }.execute();
        } else {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
