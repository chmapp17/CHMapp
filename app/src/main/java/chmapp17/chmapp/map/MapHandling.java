package chmapp17.chmapp.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapHandling {

    public static void updateMapPosition(GoogleMap googleMap, LatLng latLng) {
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}
