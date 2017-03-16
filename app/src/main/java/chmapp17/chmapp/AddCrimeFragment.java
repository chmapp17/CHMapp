package chmapp17.chmapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import chmapp17.chmapp.map.MapHandling;

public class AddCrimeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap addcMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_crime, container, false);
        ImageButton myLocationButton = (ImageButton) view.findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapHandling.updateMapPosition(getActivity(), addcMap);
            }
        });
        return view;
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
        MapHandling.updateMapPosition(getActivity(), addcMap);
    }
}
