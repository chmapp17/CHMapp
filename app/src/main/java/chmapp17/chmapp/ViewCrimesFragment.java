package chmapp17.chmapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private int cityZoom = 10;
    private CameraPosition currentCameraPosition;
    private CameraPosition previousCameraPosition;
    private MapHandling mapHandling;
    private DataBaseHandling dbHandling;
    private ScheduledExecutorService scheduleUpdateLocation;
    private View view_global;
    private boolean Marker_clicked = false;
    private String Marker_id = "";
    private CrimeReview cReviewUpdate;
    //Buttons
    private Button buttonReviewUp, buttonReviewDown;
    private ImageView viewCrimeIcon;
    private TextView viewCrimeDescription, viewCrimeType, viewLocationDescription, viewCrimeRatingText, viewCrimeDate;
    private LinearLayout l1, l2;
    private static boolean ViewFragmentClicked1 = false;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        view_global = inflater.inflate(R.layout.fragment_view_crimes, container, false);
        dbHandling = new DataBaseHandling();
        auth = FirebaseAuth.getInstance();

        Switch autoLocationSwitch = (Switch) view_global.findViewById(R.id.autoLocationSwitch);
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

        final Switch crimeHeatSwitch = (Switch) view_global.findViewById(R.id.crimeHeatSwitch);
        crimeHeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                viewcMap.clear();
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
                    viewcMap.animateCamera(CameraUpdateFactory.zoomTo(cityZoom));
                } else {
                    viewcMap.animateCamera(CameraUpdateFactory.zoomTo(savedZoom));
                }
            }
        });

        ImageButton myLocationButton = (ImageButton) view_global.findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapHandling.updateLocation(true);
            }
        });

        l1 = (LinearLayout) view_global.findViewById(R.id.layout1);
        l2 = (LinearLayout) view_global.findViewById(R.id.layout2);
        buttonReviewUp = (Button) view_global.findViewById(R.id.reviewUp);
        buttonReviewDown = (Button) view_global.findViewById(R.id.reviewDown);
        viewCrimeIcon = (ImageView) view_global.findViewById(R.id.viewCrimeIcon);
        viewCrimeDescription = (TextView) view_global.findViewById(R.id.viewCrimeDescription);
        viewCrimeType = (TextView) view_global.findViewById(R.id.viewCrimeType);
        viewCrimeDate = (TextView) view_global.findViewById(R.id.viewCrimeDate);
        viewCrimeDescription.setMovementMethod(new ScrollingMovementMethod());
        viewLocationDescription = (TextView) view_global.findViewById(R.id.viewLocationDescription);
        viewCrimeRatingText = (TextView) view_global.findViewById(R.id.viewRatingScore);

        buttonReviewUp.setVisibility(View.GONE);
        buttonReviewDown.setVisibility(View.GONE);
        viewCrimeIcon.setVisibility(View.GONE);
        viewCrimeDate.setVisibility(View.GONE);
        viewCrimeDescription.setVisibility(View.GONE);
        viewCrimeType.setVisibility(View.GONE);
        viewLocationDescription.setVisibility(View.GONE);
        viewCrimeRatingText.setVisibility(View.GONE);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);


        buttonReviewUp.setOnClickListener(new View.OnClickListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    String thisUser = auth.getCurrentUser().getUid();
                    CrimeInfo crime = mapHandling.getMarkerCrimeInfo(Marker_id);
                    if (!HasUserAddedReview(crime)) {
                        buttonReviewUp.setBackgroundResource(R.drawable.arrow_up_voted);
                        crime.cRating++;
                        crime.cReviews.add(new CrimeReview(thisUser, true));
                        dbHandling.updateCrime(crime);
                        viewCrimeRatingText.setText(String.valueOf(crime.cRating));
                        Toast.makeText(context, "Review added!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!HasUserUpVoted(crime)) {
                            buttonReviewDown.setBackgroundResource(R.drawable.arrow_down);
                            crime.cRating++;
                            int index = getIndexOfReview(crime);
                            crime.cReviews.remove(index);
                            dbHandling.updateCrime(crime);
                            viewCrimeRatingText.setText(String.valueOf(crime.cRating));
                            Toast.makeText(context, "Review removed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Review unchanged!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(context, "Can't add review. Please login first!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonReviewDown.setOnClickListener(new View.OnClickListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    String thisUser = auth.getCurrentUser().getUid();
                    CrimeInfo crime = mapHandling.getMarkerCrimeInfo(Marker_id);
                    if (!HasUserAddedReview(crime)) {
                        buttonReviewDown.setBackgroundResource(R.drawable.arrow_down_voted);
                        crime.cRating--;
                        crime.cReviews.add(new CrimeReview(thisUser, false));
                        dbHandling.updateCrime(crime);
                        viewCrimeRatingText.setText(String.valueOf(crime.cRating));
                        Toast.makeText(context, "Review added!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (HasUserUpVoted(crime)) {
                            buttonReviewUp.setBackgroundResource(R.drawable.arrow_up);
                            crime.cRating--;
                            int index = getIndexOfReview(crime);
                            crime.cReviews.remove(index);
                            dbHandling.updateCrime(crime);
                            viewCrimeRatingText.setText(String.valueOf(crime.cRating));
                            Toast.makeText(context, "Review removed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Review unchanged!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(context, "Can't add review. Please login first!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view_global;
    }

    @Override
    public void onStart() {
        super.onStart();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.viewcMapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (scheduleUpdateLocation != null)
            scheduleUpdateLocation.shutdown();
    }

    protected Runnable autoUpdateLocation = new Runnable() {
        @Override
        public void run() {
            mapHandling.updateLocation(false);
        }
    };

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
        mapHandling = new MapHandling(context, viewcMap);
        mapHandling.updateLocation(true);
        viewcMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            Switch crimeHeatSwitch = (Switch) view_global.findViewById(R.id.crimeHeatSwitch);

            @Override
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onCameraIdle() {
                currentCameraPosition = viewcMap.getCameraPosition();
                if (!currentCameraPosition.equals(previousCameraPosition) &&
                        currentCameraPosition.zoom != viewcMap.getMinZoomLevel()) {
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

        viewcMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                if (Marker_clicked == true) {
                    Marker_clicked = false;
                    buttonReviewUp.setVisibility(View.GONE);
                    buttonReviewDown.setVisibility(View.GONE);
                    viewCrimeIcon.setVisibility(View.GONE);
                    viewCrimeDescription.setVisibility(View.GONE);
                    viewCrimeType.setVisibility(View.GONE);
                    viewLocationDescription.setVisibility(View.GONE);
                    viewCrimeRatingText.setVisibility(View.GONE);
                    viewCrimeDate.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.GONE);
                }
            }
        });

        viewcMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (!arg0.getTitle().equals("My location")) {
                    if (!Marker_id.contentEquals(arg0.getId()))
                        Marker_clicked = false;

                    if (Marker_clicked == false) {
                        Marker_id = arg0.getId();
                        CrimeInfo crime = mapHandling.getMarkerCrimeInfo(Marker_id);

                        if (auth.getCurrentUser() != null) {
                            if (HasUserAddedReview(crime)) {
                                if (HasUserUpVoted(crime)) {
                                    buttonReviewUp.setBackgroundResource(R.drawable.arrow_up_voted);
                                    //buttonReviewUp.setBackgroundResource(R.drawable.arrow_up);
                                    buttonReviewDown.setBackgroundResource(R.drawable.arrow_down);
                                } else {
                                    buttonReviewDown.setBackgroundResource(R.drawable.arrow_down_voted);
                                    buttonReviewUp.setBackgroundResource(R.drawable.arrow_up);
                                    //buttonReviewDown.setBackgroundResource(R.drawable.arrow_down);
                                }
                            } else {
                                buttonReviewUp.setBackgroundResource(R.drawable.arrow_up);
                                buttonReviewDown.setBackgroundResource(R.drawable.arrow_down);
                            }
                        } else {
                            buttonReviewUp.setBackgroundResource(R.drawable.arrow_up);
                            buttonReviewDown.setBackgroundResource(R.drawable.arrow_down);
                        }
                        String title = "Crime type: " + crime.cType;
                        SpannableString content = new SpannableString(title);
                        content.setSpan(new UnderlineSpan(), 0, title.length(), 0);
                        int drawable_id = crime.getCrimeDrawableID(context, crime.cType, "icon");
                        viewCrimeIcon.setBackgroundResource(drawable_id);
                        viewCrimeType.setText(content);
                        viewCrimeDate.setText("Crime added on " + crime.cDate);
                        viewCrimeDescription.setText("Crime description: " + crime.cDescr);
                        viewLocationDescription.setText("Location description: " + crime.lDescr);
                        viewCrimeRatingText.setText(String.valueOf(crime.cRating));

                        buttonReviewUp.setVisibility(View.VISIBLE);
                        buttonReviewDown.setVisibility(View.VISIBLE);
                        viewCrimeIcon.setVisibility(View.VISIBLE);
                        viewCrimeDescription.setVisibility(View.VISIBLE);
                        viewCrimeType.setVisibility(View.VISIBLE);
                        viewLocationDescription.setVisibility(View.VISIBLE);
                        viewCrimeRatingText.setVisibility(View.VISIBLE);
                        viewCrimeDate.setVisibility(View.VISIBLE);
                        l1.setVisibility(View.VISIBLE);
                        l2.setVisibility(View.VISIBLE);

                        Marker_clicked = true;
                    } else {
                        buttonReviewUp.setVisibility(View.GONE);
                        buttonReviewDown.setVisibility(View.GONE);
                        viewCrimeIcon.setVisibility(View.GONE);
                        viewCrimeDescription.setVisibility(View.GONE);
                        viewCrimeType.setVisibility(View.GONE);
                        viewLocationDescription.setVisibility(View.GONE);
                        viewCrimeRatingText.setVisibility(View.GONE);
                        viewCrimeDate.setVisibility(View.GONE);
                        l1.setVisibility(View.GONE);
                        l2.setVisibility(View.GONE);
                        Marker_clicked = false;
                    }
                }
                return true;
            }
        });


    }

    public int getIndexOfReview(CrimeInfo crime) {
        int index = -1;
        String thisUser = auth.getCurrentUser().getUid();

        for (int i = 0; i < crime.cReviews.size(); i++) {
            if (crime.cReviews.get(i).uId.equals(thisUser)) {
                index = i;
            }
        }
        return index;

    }

    public boolean HasUserAddedReview(CrimeInfo crime) {
        boolean toReturn = false;
        String thisUser = auth.getCurrentUser().getUid();
        for (CrimeReview cReview : crime.cReviews) {
            if (cReview.uId.equals(thisUser)) {
                toReturn = true;

            }
        }
        return toReturn;
    }

    public boolean HasUserUpVoted(CrimeInfo crime) {
        boolean toReturn = false;
        String thisUser = auth.getCurrentUser().getUid();
        for (CrimeReview cReview : crime.cReviews) {
            if (cReview.uId.equals(thisUser) && cReview.hasUpVoted == true) {
                toReturn = true;

            }
        }
        return toReturn;
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
