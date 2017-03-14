package chmapp17.chmapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import chmapp17.chmapp.geolocation.GeoLocation;

public class ViewCrimesFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {
    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;

    Context ctx;
    private GoogleMap viewcMap;
    private double _longitude = 26.246355;
    private double _latitude = 47.641546;

    //Buttons

    private ImageButton buttonSave;
    private ImageButton buttonCurrent;
    private ImageButton buttonView;

    //Google ApiClient
    private GoogleApiClient googleApiClient;
    private FragmentActivity myContext = new FragmentActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_view_crimes, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.viewcmap);
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
        double[] loc = new double[2];
        final GeoLocation geoLocation = new GeoLocation();
        new AsyncTask<Void, Void, double[]>() {
            @Override
            protected double[] doInBackground(Void... voids) {

                return geoLocation.GetLocation(getActivity());
            }

            @Override
            protected void onPostExecute(double[] loc) {
                _latitude = loc[LATITUDE];
                _longitude = loc[LONGITUDE];
                String msg = _latitude + ", " + _longitude;

                // Add a marker in Sydney and move the camera
                LatLng location_position = new LatLng(_latitude, _longitude);
                viewcMap.addMarker(new MarkerOptions().position(location_position).title("Marker")).setDraggable(true);
                viewcMap.moveCamera(CameraUpdateFactory.newLatLng(location_position));
                viewcMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        }.execute();
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

