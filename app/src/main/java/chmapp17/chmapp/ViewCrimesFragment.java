package chmapp17.chmapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import chmapp17.chmapp.database.CrimeInfo;
import chmapp17.chmapp.database.CrimeReview;
import chmapp17.chmapp.database.DataBaseHandling;
import chmapp17.chmapp.map.MapHandling;

public class ViewCrimesFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {

    private Context context;
    private FirebaseAuth auth;
    private GoogleMap viewcMap;
    private CameraPosition currentCameraPosition;
    private CameraPosition previousCameraPosition;
    private MapHandling mapHandling;
    private DataBaseHandling dbHandling;
    private View view_global;
    private boolean Marker_clicked = false;
    private String Marker_id = "";
    private CrimeReview cReviewUpdate;
    //Buttons
    private Spinner spinnerReview;
    private Button buttonReviewCrime;
    private TextView viewCrimeDescription, viewCrimeType, viewCrimeReviews, viewCrimeReviewText;
    private LinearLayout l1,l2;
    private static boolean ViewFragmentClicked1 = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        view_global = inflater.inflate(R.layout.fragment_view_crimes, container, false);
        dbHandling = new DataBaseHandling();
        auth = FirebaseAuth.getInstance();

        l1 = (LinearLayout) view_global.findViewById(R.id.layout1);
        l2 = (LinearLayout) view_global.findViewById(R.id.layout2);
        spinnerReview = (Spinner) view_global.findViewById(R.id.spinnerReview);
        ArrayAdapter<CharSequence> Radapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.review_array, android.R.layout.simple_spinner_item);
        Radapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReview.setAdapter(Radapter);

        buttonReviewCrime = (Button) view_global.findViewById(R.id.buttonReviewCrime);
        viewCrimeDescription = (TextView) view_global.findViewById(R.id.viewCrimeDescription);
        viewCrimeType = (TextView) view_global.findViewById(R.id.viewCrimeType);
        viewCrimeDescription.setMovementMethod(new ScrollingMovementMethod());
        viewCrimeReviews = (TextView) view_global.findViewById(R.id.viewCrimeReviews);
        viewCrimeReviewText= (TextView) view_global.findViewById(R.id.viewTextReview);

        buttonReviewCrime.setVisibility(View.GONE);
        viewCrimeDescription.setVisibility(View.GONE);
        viewCrimeType.setVisibility(View.GONE);
        viewCrimeReviews.setVisibility(View.GONE);
        viewCrimeReviewText.setVisibility(View.GONE);
        spinnerReview.setVisibility(View.GONE);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);

        buttonReviewCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    CrimeInfo crime = mapHandling.getMarkerCrimeInfo(Marker_id);
                    int starRating = spinnerReview.getSelectedItemPosition() + 1;
                    cReviewUpdate = new CrimeReview(crime.cId,auth.getCurrentUser().getUid(),starRating);
                    if(!HasUserAddedReview(crime))
                    {
                        crime.toCalcRating = true;
                        dbHandling.updateCrime(crime);
                        dbHandling.addReview(cReviewUpdate);
                        Toast.makeText(context, "Review added!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        crime.toCalcRating = true;
                        dbHandling.updateCrime(crime);
                        dbHandling.updateReview(cReviewUpdate);
                        Toast.makeText(context, "Review updated!", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(context, "Can't add review. Please login first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view_global;
    }
    public boolean HasUserAddedReview(CrimeInfo crime)
    {
        boolean toReturn = false;
        String thisUser = auth.getCurrentUser().getUid();
        String cId = crime.cId;
        for (CrimeReview cReview : MainActivity.reviewList) {
            if (cReview.uId.equals(thisUser) && cReview.cId.equals(cId)){
                toReturn = true;
                cReviewUpdate.rId = cReview.rId;
            }
        }
        return toReturn;
    }

    @Override
    public void onStart() {
        super.onStart();
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
                    buttonReviewCrime.setVisibility(View.GONE);
                    viewCrimeDescription.setVisibility(View.GONE);
                    viewCrimeType.setVisibility(View.GONE);
                    viewCrimeReviews.setVisibility(View.GONE);
                    viewCrimeReviewText.setVisibility(View.GONE);
                    spinnerReview.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.GONE);
                }
            }
        });

        viewcMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                // if(arg0.getTitle().equals("MyHome")) // if marker source is clicked
                if (!arg0.getTitle().toString().equals(context.getResources().getString(R.string.CurrentLocationMarker))) {
                    if (!Marker_id.contentEquals(arg0.getId()))
                        Marker_clicked = false;

                    if (Marker_clicked == false) {
                        dbHandling.crimeRatingCalc();
                        Marker_id = arg0.getId();
                        CrimeInfo crime = mapHandling.getMarkerCrimeInfo(Marker_id);
                        String title = "Crime type: " + crime.cType;
                        SpannableString content = new SpannableString(title);
                        content.setSpan(new UnderlineSpan(), 0, title.length(), 0);
                        viewCrimeType.setText(content);
                        viewCrimeDescription.setText("Crime description: " + crime.cDescr);
                        viewCrimeReviews.setText("Average Rating: " + crime.cRating + " Stars");

                        buttonReviewCrime.setVisibility(View.VISIBLE);
                        viewCrimeDescription.setVisibility(View.VISIBLE);
                        viewCrimeType.setVisibility(View.VISIBLE);
                        viewCrimeReviews.setVisibility(View.VISIBLE);
                        viewCrimeReviewText.setVisibility(View.VISIBLE);
                        spinnerReview.setVisibility(View.VISIBLE);
                        l1.setVisibility(View.VISIBLE);
                        l2.setVisibility(View.VISIBLE);

                        Marker_clicked = true;
                    } else {
                        buttonReviewCrime.setVisibility(View.GONE);
                        viewCrimeDescription.setVisibility(View.GONE);
                        viewCrimeType.setVisibility(View.GONE);
                        viewCrimeReviews.setVisibility(View.GONE);
                        viewCrimeReviewText.setVisibility(View.GONE);
                        spinnerReview.setVisibility(View.GONE);
                        l1.setVisibility(View.GONE);
                        l2.setVisibility(View.GONE);
                        Marker_clicked = false;
                    }
                }
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
