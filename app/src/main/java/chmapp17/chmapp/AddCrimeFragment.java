package chmapp17.chmapp;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chmapp17.chmapp.database.CrimeInfo;
import chmapp17.chmapp.database.DataBaseHandling;
import chmapp17.chmapp.map.MapHandling;

public class AddCrimeFragment extends Fragment implements OnMapReadyCallback {

    private Context context;
    private GoogleMap addcMap;
    private MapHandling mapHandling = new MapHandling();
    private DataBaseHandling dbHandling = new DataBaseHandling();
    private ScheduledExecutorService scheduleUpdateLocation;
    private ArrayList<CrimeInfo> crimes;

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
                mapHandling.updateLocation(context, addcMap);
            }
        });

        final Spinner spinnerCrimes = (Spinner) view.findViewById(R.id.spinnerCrimes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.crimes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrimes.setAdapter(adapter);

        final EditText editDate = (EditText) view.findViewById(R.id.editDate);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        editDate.setText(dateFormat.format(Calendar.getInstance().getTime()));

        final EditText editCrimeDescr = (EditText) view.findViewById(R.id.editCrimeDescr);
        final EditText editLocationDescr = (EditText) view.findViewById(R.id.editLocationDescr);

        Button buttonAddCrime = (Button) view.findViewById(R.id.buttonAddCrime);
        buttonAddCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandling.addCrime(new CrimeInfo(spinnerCrimes.getSelectedItem().toString(),
                        editDate.getText().toString(),
                        editCrimeDescr.getText().toString(),
                        editLocationDescr.getText().toString(),
                        mapHandling.getCurrentLocation().getLatitude() + ", " +
                                mapHandling.getCurrentLocation().getLongitude()));
                Toast.makeText(getActivity(), "Crime added", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        context = getContext();
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
        mapHandling.updateLocation(context, addcMap);
        crimes = dbHandling.getCrimes();
        mapHandling.showCrimes(addcMap, crimes);
        for (CrimeInfo crime : crimes) {
            Toast.makeText(getActivity(), "Crime: " + crime, Toast.LENGTH_SHORT).show();
        }
    }

    protected Runnable autoUpdateLocation = new Runnable() {
        @Override
        public void run() {
            mapHandling.updateLocation(context, addcMap);
        }
    };
}
