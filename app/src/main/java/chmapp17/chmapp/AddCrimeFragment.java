package chmapp17.chmapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import chmapp17.chmapp.geolocation.GeoLocation;

public class AddCrimeFragment extends Fragment implements OnMapReadyCallback {

    private final int LAT = 0;
    private final int LONG = 1;

    private GoogleMap addcMap;
    private double latitude = 0;
    private double longitude = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_crime, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addcmap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        addcMap = googleMap;
        final GeoLocation geoLocation = new GeoLocation();
        new AsyncTask<Void, Void, double[]>() {
            @Override
            protected double[] doInBackground(Void... voids) {
                return geoLocation.GetLocation(getActivity());
            }

            @Override
            protected void onPostExecute(double[] loc) {
                latitude = loc[LAT];
                longitude = loc[LONG];

                LatLng location_position = new LatLng(latitude, longitude);
                addcMap.addMarker(new MarkerOptions().position(location_position).title("Marker")).setDraggable(true);
                addcMap.moveCamera(CameraUpdateFactory.newLatLng(location_position));
                addcMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        }.execute();
    }
}