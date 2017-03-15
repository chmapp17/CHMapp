package chmapp17.chmapp.map;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapHandling {

    public static void updateMapPosition(GoogleMap googleMap, LatLng latLng, double accuracy) {
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.addCircle(new CircleOptions().center(latLng).radius(accuracy)
                .strokeColor(Color.RED).strokeWidth(3).fillColor(0x50ff0000));
    }
}
