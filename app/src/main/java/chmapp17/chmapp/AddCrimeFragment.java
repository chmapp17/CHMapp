package chmapp17.chmapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chmapp17.chmapp.map.MapHandling;

public class AddCrimeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap addcMap;
    private MapHandling mapHandling = new MapHandling();
    private ScheduledExecutorService scheduleUpdateLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_crime, container, false);

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

        ImageButton myLocationButton = (ImageButton) view.findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapHandling.updateMapPosition(getActivity(), addcMap);
            }
        });

        Spinner spinnerCrimes = (Spinner) view.findViewById(R.id.spinnerCrimes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.crimes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrimes.setAdapter(adapter);

        EditText editDate = (EditText) view.findViewById(R.id.editDate);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        editDate.setText(dateFormat.format(Calendar.getInstance().getTime()));

        Button buttonAddCrime = (Button) view.findViewById(R.id.buttonAddCrime);
        buttonAddCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Crime added", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addcMapFragment);
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
        mapHandling.updateMapPosition(getActivity(), addcMap);
    }

    protected Runnable autoUpdateLocation = new Runnable() {

        @Override
        public void run() {
            if (MainActivity.isAppVisible) {
                mapHandling.updateMapPosition(getActivity(), addcMap);
            }
        }
    };
}
