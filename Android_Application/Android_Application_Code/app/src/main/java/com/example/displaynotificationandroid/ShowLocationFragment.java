package com.example.displaynotificationandroid;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowLocationFragment extends Fragment {

    MapView mapView;
    private GoogleMap googleMap;

    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String TAG = "LocationTag";
    private static final int ERROE_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final float DEFAULT_ZOOM = 16f;
    private FusedLocationProviderClient mFusedLocationProviderClient;
//    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private Boolean mLocationPermissionGranted = false;

    String latitude, longitude;
    Double new_latitude, new_longitude;

    AutoCompleteTextView sreachBar;
    ImageView gps_Id;

    public static ShowLocationFragment newInstance(String latitude, String longitude) {
        ShowLocationFragment showLocationFragment = new ShowLocationFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_LATITUDE, latitude);
        arg.putString(ARG_LONGITUDE, longitude);

        showLocationFragment.setArguments(arg);
        return showLocationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_location, container, false);

        sreachBar = view.findViewById(R.id.sreachBar);

        if (getArguments() != null) {
            latitude = getArguments().getString("latitude");
            longitude = getArguments().getString("longitude");
        }

        new_latitude = Double.parseDouble(latitude);
        new_longitude = Double.parseDouble(longitude);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        googleMap = mapView.getMap();
//        // latitude and longitude
//        double latitude = 17.385044;
//        double longitude = 78.486671;
//
//        // create marker
//        MarkerOptions marker = new MarkerOptions().position(
//                new LatLng(latitude, longitude)).title("Hello Maps");
//
//        // Changing marker icon
//        marker.icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//
//        // adding marker
//        googleMap.addMarker(marker);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));

//        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter();

        gps_Id = view.findViewById(R.id.gps_Id);

        gps_Id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyCurrentLocation();
            }
        });

        sreachBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == event.ACTION_DOWN || event.getAction() == event.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });

        if (isServicesOk()) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

//                    try {
//                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_one));
//
//                        if(!success){
//                            Log.e("MyTag", "Style parsing failed");
//                        }
//                    }
//                    catch (Resources.NotFoundException e){
//                        Log.e("MyTag", "Can't find style. Error: " + e);
//                    }

                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    googleMap.setMyLocationEnabled(true);

//                to block my location icon
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);

//                    Location currentLocation = (Location)

//                    final Task location = mFusedLocationProviderClient.getLastLocation();
//                    location.addOnCompleteListener(new OnCompleteListener() {
//                        @Override
//                        public void onComplete(@NonNull Task task) {
//
//                        }
//                    });

                    // For dropping a marker at a point on the Map
//                    LatLng sydney = new LatLng(new_latitude, new_longitude);
                    LatLng sydney = new LatLng(17.420192, 78.409622); // dummy location
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("Destination Location").snippet("Developer Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(18).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }

        return view;
    }

    private void getMyCurrentLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();

                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                    }
                }
            });
        }
        catch (SecurityException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideSoftKeyboard(){
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        Toast.makeText(getContext(), "hideSoftKeyboard", Toast.LENGTH_SHORT).show();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void geoLocate() {
        String searchString = sreachBar.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Toast.makeText(getContext(), "Location Found: " + address.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "geoLocate: Location Found: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

            hideSoftKeyboard();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        hideSoftKeyboard();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location") || !title.equals("my location") || !title.equals("MY LOCATION")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            googleMap.addMarker(options);
        }
        else{
            Toast.makeText(getContext(), "entered location: " + title, Toast.LENGTH_SHORT).show();
            getMyCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    for (int i=0; i<grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void init(){

    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services version");
        
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOk: Google Play Services is working");
            return true;
        } 
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOk: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROE_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getContext(), "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
