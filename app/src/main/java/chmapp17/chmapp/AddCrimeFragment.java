package chmapp17.chmapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import chmapp17.chmapp.geolocation.GeoLocation;
import chmapp17.chmapp.map.MapHandling;

public class AddCrimeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap addcMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_crime, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addcmap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        addcMap = googleMap;
        final GeoLocation geoLocation = new GeoLocation();
        new AsyncTask<Void, Void, LatLng>() {
            @Override
            protected LatLng doInBackground(Void... voids) {
                return geoLocation.GetLocation(getActivity());
            }

            @Override
            protected void onPostExecute(LatLng latLng) {
                MapHandling.updateMapPosition(addcMap, latLng);
            }
        }.execute();
    }
}
