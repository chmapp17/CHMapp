package chmapp17.chmapp.map;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import chmapp17.chmapp.MainActivity;
import chmapp17.chmapp.R;
import chmapp17.chmapp.database.CrimeInfo;
import chmapp17.chmapp.geolocation.GeoLocation;

public class MapHandling {

    private Location currentLocation;

    public void updateLocation(final Context context, final GoogleMap googleMap) {
        if (MainActivity.isNetworkAvailable(context)) {
            final GeoLocation geoLocation = new GeoLocation();
            new AsyncTask<Void, Void, Location>() {
                @Override
                protected Location doInBackground(Void... voids) {
                    return geoLocation.getLocation(context);
                }

                @Override
                protected void onPostExecute(Location location) {
                    currentLocation = location;
                    googleMap.clear();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    float currZoom = googleMap.getCameraPosition().zoom;
                    if (currZoom == 2) {
                        googleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 16)));
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.fromLatLngZoom(latLng, currZoom)));
                    }
                    googleMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                    googleMap.addCircle(new CircleOptions()
                            .center(latLng).radius(location.getAccuracy())
                            .fillColor(Color.argb(30, 0, 155, 255))
                            .strokeColor(Color.argb(255, 0, 155, 255)).strokeWidth(2));
                }
            }.execute();
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void showCrimes(GoogleMap googleMap, ArrayList<CrimeInfo> crimes) {

    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
