package com.techease.whereyou.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techease.whereyou.ui.models.ReviewLocation;
import com.techease.whereyou.R;
import com.techease.whereyou.utils.AlertsUtils;
import com.techease.whereyou.utils.InternetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment implements LocationListener {

    @BindView(R.id.mapView)
    MapView mMapView;


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    boolean bolFlag = false;
    private GoogleMap googleMap;
    Unbinder unbinder;
    LatLng latLng;
    List<ReviewLocation> reviewLocationsList = new ArrayList<>();
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    String Address;
    float ratingBarValue;
    FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    CameraPosition cameraPosition;
    android.support.v7.app.AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, v);
        //Declaration

        if (alertDialog == null)
            alertDialog = AlertsUtils.createProgressDialog(getActivity());


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("ReviewLocation");
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ReviewLocation reviewLocation = dataSnapshot1.getValue(ReviewLocation.class);
                    if (alertDialog != null)
                        alertDialog.dismiss();
                    showMarker(reviewLocation);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return v;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Location");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Place: ", place.getAddress().toString());
                Address = String.valueOf(place.getAddress());
                latLng = place.getLatLng();
                MyMethod(googleMap, latLng);
                //  strLocation = place.getAddress().toString();
                bolFlag = true;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        if (InternetUtils.isNetworkConnected(getActivity())) {
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();


            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    googleMap.setMyLocationEnabled(true);

                }
            });

        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place: " + place.getName());
                //  strLocation = place.getAddress().toString();
                bolFlag = true;
                MyMethod(googleMap, place.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void showMarker(ReviewLocation reviewLocation) {


        LatLng latLng = new LatLng(reviewLocation.getLat(), reviewLocation.getLon());
        Log.d("zmaLatlng",latLng.toString());
        if (latLng!=null)
        {
            Marker m;
            m = googleMap.addMarker(new MarkerOptions().position(latLng).title(reviewLocation.getLocationName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_location)));
        }
        else
        {
            Toast.makeText(getActivity(), "You have not searched places", Toast.LENGTH_SHORT).show();
        }


        // For zooming automatically to the location of the marker
//        cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void MyMethod(GoogleMap googleMap, final LatLng latLng) {
        googleMap = googleMap;
        LatLng location = latLng;
        googleMap.addMarker(new MarkerOptions().position(location).title(Address));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // AlertsUtils.showMarkerDialog(getActivity(),marker.getTitle());
                showMarkerDialog(getActivity(), marker.getTitle(), latLng);
                return false;
            }

        });

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        if (googleMap != null) {
            googleMap.animateCamera(cameraUpdate);
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public static void showMarkerDialog(final Activity activity, final String message, final LatLng latLng) {


        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog
                , null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView tvTown = dialogView.findViewById(R.id.tvTownCustomDialog);
        tvTown.setText(message);
        Button btnReview = dialogView.findViewById(R.id.btnReviewCustomDialog);
        Button btnExisting = dialogView.findViewById(R.id.btnExistingCustomDialog);
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewDialog(activity, message, latLng);
                alertDialog.dismiss();
            }
        });
        btnExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(activity, "done", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }


    public static void showReviewDialog(final Activity activity, final String message, final LatLng latLngObject) {

        final String strTextBox;
        final float value;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_reviewdialog
                , null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView tvTown = dialogView.findViewById(R.id.tvAddress);
        tvTown.setText(message);

        Button btnReview = dialogView.findViewById(R.id.btnSubmitCustomDialogReview);
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingbar);
        final EditText editText = (EditText) dialogView.findViewById(R.id.etTextBox);
        strTextBox = editText.getText().toString();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float v, boolean b) {
                ratingBar.setRating(v);

            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth;
                firebaseAuth = FirebaseAuth.getInstance();
                String Uid = firebaseAuth.getUid();
                ReviewLocation reviewLocation = new ReviewLocation(Uid, message, editText.getText().toString(),latLngObject.latitude, latLngObject.longitude, ratingBar.getRating());
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("ReviewLocation").child(message).setValue(reviewLocation);
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }
}
