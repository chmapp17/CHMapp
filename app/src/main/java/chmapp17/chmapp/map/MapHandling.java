package chmapp17.chmapp.map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import chmapp17.chmapp.MainActivity;
import chmapp17.chmapp.geolocation.GeoLocation;

public class MapHandling {

    public static void updateMapPosition(FragmentActivity activity, GoogleMap googleMap) {
        if (MainActivity.isNetworkAvailable(activity)) {
            final GeoLocation geoLocation = new GeoLocation();
            final FragmentActivity lActivity = activity;
            final GoogleMap lGoogleMap = googleMap;
            new AsyncTask<Void, Void, LatLng>() {
                @Override
                protected LatLng doInBackground(Void... voids) {
                    return geoLocation.GetLocation(lActivity);
                }

                @Override
                protected void onPostExecute(LatLng latLng) {
                    lGoogleMap.clear();
                    float currZoom = lGoogleMap.getCameraPosition().zoom;
                    if (currZoom == 2) {
                        lGoogleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 15)));
                    } else {
                        lGoogleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.fromLatLngZoom(latLng, currZoom)));
                    }
                    lGoogleMap.addMarker(new MarkerOptions().position(latLng));
                    lGoogleMap.addCircle(new CircleOptions()
                            .center(latLng).radius(geoLocation.GetAccuracy())
                            .strokeColor(Color.RED).strokeWidth(3).fillColor(0x50ff0000));
                }
            }.execute();
        } else {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
