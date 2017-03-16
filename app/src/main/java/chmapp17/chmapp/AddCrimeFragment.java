package chmapp17.chmapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chmapp17.chmapp.map.MapHandling;

public class AddCrimeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap addcMap;
    private ScheduledExecutorService scheduleUpdateLocation;

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
        Switch autoLocationSwitch = (Switch) view.findViewById(R.id.autoLocationSwitch);
        autoLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    scheduleUpdateLocation = Executors.newSingleThreadScheduledExecutor();
                    scheduleUpdateLocation
                            .scheduleAtFixedRate(autoUpdateLocation, 0, 10, TimeUnit.SECONDS);
                } else {
                    if (scheduleUpdateLocation != null)
                        scheduleUpdateLocation.shutdown();
                }
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
    public void onStop() {
        super.onStop();
        if (scheduleUpdateLocation != null)
            scheduleUpdateLocation.shutdown();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        addcMap = googleMap;
        MapHandling.updateMapPosition(getActivity(), addcMap);
    }

    protected Runnable autoUpdateLocation = new Runnable() {

        @Override
        public void run() {
            if (MainActivity.isAppVisible) {
                MapHandling.updateMapPosition(getActivity(), addcMap);
            }
        }
    };
}
