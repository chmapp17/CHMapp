package chmapp17.chmapp.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import chmapp17.chmapp.MainActivity;
import chmapp17.chmapp.R;
import chmapp17.chmapp.database.CrimeInfo;
import chmapp17.chmapp.geolocation.GeoLocation;

public class MapHandling {

    private Location currentLocation;
    private Marker bluedotMarker;
    private Circle accuracyCircle;

    public void updateLocation(final Context context, final GoogleMap googleMap, final boolean showCrimes) {
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
                    if (bluedotMarker != null) {
                        bluedotMarker.remove();
                        accuracyCircle.remove();
                    }
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    float currZoom = googleMap.getCameraPosition().zoom;
                    if (currZoom == 2) {
                        googleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 16)));
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.fromLatLngZoom(latLng, currZoom)));
                    }
                    bluedotMarker = googleMap.addMarker(new MarkerOptions()
                            .position(latLng).anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                    accuracyCircle = googleMap.addCircle(new CircleOptions()
                            .center(latLng).radius(location.getAccuracy())
                            .fillColor(Color.argb(30, 0, 155, 255))
                            .strokeColor(Color.argb(255, 0, 155, 255)).strokeWidth(2));

                    if (showCrimes) {
                        MainActivity.crimesShown = true;
                        Location crimeLocation = new Location("crime");
                        for (CrimeInfo crime : MainActivity.crimes) {
                            String[] coord = crime.cLocation.replace(",", "").split(" ");
                            crimeLocation.setLatitude(Double.parseDouble(coord[0]));
                            crimeLocation.setLongitude(Double.parseDouble(coord[1]));

                            Bitmap smallMarker = resizeMapMarker(context,"img_"+ crime.cType.toLowerCase(), 100, 100);

                            if (currentLocation != null &&
                                    currentLocation.distanceTo(crimeLocation) < 10000) {
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(crimeLocation.getLatitude(),
                                                crimeLocation.getLongitude()))
                                        .title(crime.cType)
                                        .snippet("Date: " + crime.cDate +
                                                " Crime description: " + crime.cDescr +
                                                " Location description: " + crime.lDescr)
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                            }
                        }
                    }
                }
            }.execute();
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    private Bitmap resizeMapMarker(final Context context, String imgName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(imgName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);

        return resizedBitmap;
    }

}
