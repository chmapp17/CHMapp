package chmapp17.chmapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chmapp17.chmapp.database.CrimeInfo;
import chmapp17.chmapp.database.DataBaseHandling;
import chmapp17.chmapp.map.MapHandling;

public class AddCrimeFragment extends Fragment implements OnMapReadyCallback {

    private View view;
    private Context context;
    private FirebaseAuth auth;
    private GoogleMap addcMap;
    private int cityZoom = 10;
    private CameraPosition currentCameraPosition;
    private CameraPosition previousCameraPosition;
    private MapHandling mapHandling;
    private DataBaseHandling dbHandling;
    private ScheduledExecutorService scheduleUpdateLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_crime, container, false);
        context = getContext();
        dbHandling = new DataBaseHandling();
        auth = FirebaseAuth.getInstance();

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

        final Switch crimeHeatSwitch = (Switch) view.findViewById(R.id.crimeHeatSwitch);
        crimeHeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                addcMap.clear();
                if (on) {
                    mapHandling.addCrimeHeatOverlay();
                } else {
                    mapHandling.removeCrimeHeatOverlay();
                    mapHandling.showCrimes(true);
                    mapHandling.updateLocation(false);
                }
            }
        });
        crimeHeatSwitch.setOnClickListener(new View.OnClickListener() {
            float savedZoom = 0;

            @Override
            public void onClick(View v) {
                if (crimeHeatSwitch.isChecked() && currentCameraPosition.zoom > cityZoom) {
                    savedZoom = currentCameraPosition.zoom;
                    addcMap.animateCamera(CameraUpdateFactory.zoomTo(cityZoom));
                } else {
                    addcMap.animateCamera(CameraUpdateFactory.zoomTo(savedZoom));
                }
            }
        });

        ImageButton myLocationButton = (ImageButton) view.findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapHandling.updateLocation(true);
            }
        });

        final ImageView crimeIcon = (ImageView) view.findViewById(R.id.crimeIcon);

        final Spinner spinnerCrimes = (Spinner) view.findViewById(R.id.spinnerCrimes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.crimes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrimes.setAdapter(adapter);
        spinnerCrimes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int drawable_id = context.getResources().getIdentifier
                        ("crime" + (pos + 1) + "_" + "icon", "drawable", context.getPackageName());
                crimeIcon.setBackgroundResource(drawable_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final EditText editDate = (EditText) view.findViewById(R.id.editDate);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy @ HH:mm");
        editDate.setText(dateFormat.format(Calendar.getInstance().getTime()));

        final EditText editCrimeDescr = (EditText) view.findViewById(R.id.editCrimeDescr);
        final EditText editLocationDescr = (EditText) view.findViewById(R.id.editLocationDescr);

        Button buttonAddCrime = (Button) view.findViewById(R.id.buttonAddCrime);
        buttonAddCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                double lat = mapHandling.getCurrentLocation().getLatitude();
                double lng = mapHandling.getCurrentLocation().getLongitude();
                CrimeInfo crime = new CrimeInfo(spinnerCrimes.getSelectedItem().toString(),
                        editDate.getText().toString(),
                        editCrimeDescr.getText().toString(),
                        editLocationDescr.getText().toString(),
                        lat + ", " + lng,
                        auth.getCurrentUser().getUid());
                dbHandling.addCrime(crime);
                int drawable_id = crime.getCrimeDrawableID(context, crime.cType, "pin");
                addcMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .icon(drawable_id == 0 ?
                                BitmapDescriptorFactory.defaultMarker() :
                                mapHandling.getMarkerIconFromDrawable(context.getDrawable(drawable_id)))
                        .title(crime.cType)
                        .snippet(crime.cDate));
                Toast.makeText(context, "Crime added", Toast.LENGTH_SHORT).show();
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
        mapHandling = new MapHandling(context, addcMap);
        mapHandling.updateLocation(true);
        addcMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            Switch crimeHeatSwitch = (Switch) view.findViewById(R.id.crimeHeatSwitch);

            @Override
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onCameraIdle() {
                currentCameraPosition = addcMap.getCameraPosition();
                if (!currentCameraPosition.equals(previousCameraPosition) &&
                        currentCameraPosition.zoom != addcMap.getMinZoomLevel()) {
                    if (currentCameraPosition.zoom <= cityZoom)
                        crimeHeatSwitch.setChecked(true);
                    else
                        crimeHeatSwitch.setChecked(false);
                    if (crimeHeatSwitch.isChecked())
                        mapHandling.addCrimeHeatOverlay();
                    else
                        mapHandling.showCrimes(false);
                }
                previousCameraPosition = currentCameraPosition;
            }
        });
    }

    protected Runnable autoUpdateLocation = new Runnable() {
        @Override
        public void run() {
            mapHandling.updateLocation(false);
        }
    };
}
