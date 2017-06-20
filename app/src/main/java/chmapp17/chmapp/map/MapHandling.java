package chmapp17.chmapp.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                        if (!MainActivity.crimes.isEmpty()) {
                            MainActivity.crimesShown = true;
                            Location crimeLocation = new Location("crime");
                            for (CrimeInfo crime : MainActivity.crimes) {
                                String[] coord = crime.cLocation.replace(",", "").split(" ");
                                crimeLocation.setLatitude(Double.parseDouble(coord[0]));
                                crimeLocation.setLongitude(Double.parseDouble(coord[1]));
                                if (currentLocation != null &&
                                        currentLocation.distanceTo(crimeLocation) < 10000) {
                                    int drawable_id = getCrimeDrawableID(context, crime.cType);
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(crimeLocation.getLatitude(),
                                                    crimeLocation.getLongitude()))
                                            .icon(drawable_id == 0 ?
                                                    BitmapDescriptorFactory.defaultMarker() :
                                                    getMarkerIconFromDrawable(context.getDrawable(drawable_id)))
                                            .title(crime.cType)
                                            .snippet("Date: " + crime.cDate));
                                }
                            }
                        } else {
                            Toast.makeText(context, "Waiting for crime data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }.execute();
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap
                (drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public int getCrimeDrawableID(Context context, String cStr) {
        String[] crimes_array = context.getResources().getStringArray(R.array.crimes_array);
        int i = 0, drawable_id = 0;
        for (String cType : crimes_array) {
            i += 1;
            if (cType.equals(cStr))
                drawable_id = context.getResources()
                        .getIdentifier("crime_pin_" + i, "drawable", context.getPackageName());
        }
        return drawable_id;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
