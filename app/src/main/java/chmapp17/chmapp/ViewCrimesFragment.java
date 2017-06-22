package chmapp17.chmapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import chmapp17.chmapp.database.CrimeInfo;
import chmapp17.chmapp.database.DataBaseHandling;
import chmapp17.chmapp.map.MapHandling;

public class ViewCrimesFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {

    private Context context;
    private GoogleMap viewcMap;
    private CameraPosition currentCameraPosition;
    private CameraPosition previousCameraPosition;
    private MapHandling mapHandling;
    private DataBaseHandling dbHandling;
    private View view_global;
    private boolean Marker_clicked = false;
    private String Marker_id = "";
    //Buttons

    private ImageButton buttonSave;
    private ImageButton buttonCurrent;
    private ImageButton buttonView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_global = inflater.inflate(R.layout.fragment_view_crimes, container, false);
        dbHandling = new DataBaseHandling();

        Button buttonReviewCrime = (Button) view_global.findViewById(R.id.buttonReviewCrime);
        buttonReviewCrime.setVisibility(View.GONE);
        TextView viewCrimeDescription = (TextView) view_global.findViewById(R.id.viewCrimeDescription);
        viewCrimeDescription.setVisibility(View.GONE);
        TextView viewCrimeType = (TextView) view_global.findViewById(R.id.viewCrimeType);
        viewCrimeType.setVisibility(View.GONE);
        viewCrimeDescription.setMovementMethod(new ScrollingMovementMethod());
        return view_global;
    }

    @Override
    public void onStart() {
        super.onStart();
        context = getContext();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.viewcMapFragment);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        viewcMap = googleMap;
        mapHandling = new MapHandling(viewcMap);
        mapHandling.updateLocation(context, true);
        viewcMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                currentCameraPosition = viewcMap.getCameraPosition();
                if (!currentCameraPosition.equals(previousCameraPosition) && currentCameraPosition.zoom != 2) {
                    mapHandling.updateLocation(context, false);
                    mapHandling.showCrimes(context);
                }
                previousCameraPosition = currentCameraPosition;
            }
        });

        viewcMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                if (Marker_clicked == true) {
                    Marker_clicked = false;
                    Button buttonReviewCrime = (Button) view_global.findViewById(R.id.buttonReviewCrime);
                    buttonReviewCrime.setVisibility(View.GONE);
                    TextView viewCrimeDescription = (TextView) view_global.findViewById(R.id.viewCrimeDescription);
                    viewCrimeDescription.setVisibility(View.GONE);
                    TextView viewCrimeType = (TextView) view_global.findViewById(R.id.viewCrimeType);
                    viewCrimeType.setVisibility(View.GONE);
                }
            }
        });

        viewcMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                // if(arg0.getTitle().equals("MyHome")) // if marker source is clicked
                if (!Marker_id.contentEquals(arg0.getId()))
                    Marker_clicked = false;

                if (Marker_clicked == false) {
                    Marker_id = arg0.getId();
                    CrimeInfo crime = mapHandling.getMarkerCrimeInfo(Marker_id);

                    Button buttonReviewCrime = (Button) view_global.findViewById(R.id.buttonReviewCrime);
                    buttonReviewCrime.setVisibility(View.VISIBLE);

                    TextView viewCrimeType = (TextView) view_global.findViewById(R.id.viewCrimeType);
                    viewCrimeType.setText("Crime type: " + crime.cType);
                    viewCrimeType.setVisibility(View.VISIBLE);

                    TextView viewCrimeDescription = (TextView) view_global.findViewById(R.id.viewCrimeDescription);
                    viewCrimeDescription.setText("Crime description: " + crime.cDescr);
                    viewCrimeDescription.setVisibility(View.VISIBLE);

                    crime.cRating += 1;
                    dbHandling.updateCrime(crime);

                    Toast.makeText(context, "rating: " + crime.cRating, Toast.LENGTH_SHORT).show();
                    Marker_clicked = true;
                } else {
                    Button buttonReviewCrime = (Button) view_global.findViewById(R.id.buttonReviewCrime);
                    buttonReviewCrime.setVisibility(View.GONE);
                    TextView viewCrimeDescription = (TextView) view_global.findViewById(R.id.viewCrimeDescription);
                    viewCrimeDescription.setVisibility(View.GONE);
                    TextView viewCrimeType = (TextView) view_global.findViewById(R.id.viewCrimeType);
                    viewCrimeType.setVisibility(View.GONE);
                    Marker_clicked = false;
                }

                // if (!MainActivity.crimeList.isEmpty())

                return true;
            }
        });


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
