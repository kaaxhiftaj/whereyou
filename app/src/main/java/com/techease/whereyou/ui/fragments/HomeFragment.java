package com.techease.whereyou.ui.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techease.whereyou.R;
import com.techease.whereyou.controllers.AppController;
import com.techease.whereyou.ui.activities.ChatActivity;
import com.techease.whereyou.ui.models.ReviewLocation;
import com.techease.whereyou.utils.AlertsUtils;
import com.techease.whereyou.utils.Haversine;
import com.techease.whereyou.utils.InternetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment implements LocationListener {


    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    MapView mMapView;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    boolean bolFlag = false;
    Unbinder unbinder;
    LatLng latLng;
    List<ReviewLocation> reviewLocationsList = new ArrayList<>();
    String Address;
    float ratingBarValue;
    FirebaseAuth mAuth;
    CameraPosition cameraPosition;
    FirebaseUser firebaseUser;
    android.support.v7.app.AlertDialog alertDialog;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    PlaceAutocompleteFragment autocompleteFragment;
    MapFragment mapFragment;
    boolean hasPoints = false;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private DatabaseReference mFirebaseDatabase;
    private HashMap<String, String> mHashMap = new HashMap<String, String>();
    Location myCurrentLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        //Declaration

        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return v;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);


        autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Location");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Place: ", place.getAddress().toString());
                zoomToSearchedLocation(place);
                bolFlag = true;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        if (InternetUtils.isNetworkConnected(getActivity())) {


            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                    getLocation();

                }
            });

        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void getLocation() {
        if (alertDialog == null)
            alertDialog = AlertsUtils.createProgressDialog(getActivity());


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("ReviewLocation");
        firebaseUser = mAuth.getCurrentUser();
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ReviewLocation reviewLocation = dataSnapshot1.getValue(ReviewLocation.class);

                    if (alertDialog != null)
                        alertDialog.dismiss();
                    mHashMap.put(showMarker(reviewLocation).getId(), dataSnapshot1.getKey());
                    hasPoints = true;
                }
                if (hasPoints) {
                    LatLngBounds bounds = builder.build();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("HOME");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getChildFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
        getChildFragmentManager().beginTransaction().remove(mapFragment).commit();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place: " + place.getName());
                //  strLocation = place.getAddress().toString();
                bolFlag = true;
                zoomToSearchedLocation(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public Marker showMarker(ReviewLocation reviewLocation) {


        LatLng latLng = new LatLng(reviewLocation.getLat(), reviewLocation.getLon());
        builder.include(latLng);
        Log.d("zmaLatlng", latLng.toString());
        if (latLng != null) {
            Marker m = googleMap.addMarker(new MarkerOptions().position(latLng).title(reviewLocation.getLocationName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_location)));
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    final String id = mHashMap.get(marker.getId());
                    Place place = new Place() {
                        @Override
                        public String getId() {
                            return id;
                        }

                        @Override
                        public List<Integer> getPlaceTypes() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public CharSequence getAddress() {
                            return marker.getTitle();
                        }

                        @Override
                        public Locale getLocale() {
                            return null;
                        }

                        @Override
                        public CharSequence getName() {
                            return null;
                        }

                        @Override
                        public LatLng getLatLng() {
                            return marker.getPosition();
                        }

                        @Nullable
                        @Override
                        public LatLngBounds getViewport() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Uri getWebsiteUri() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public CharSequence getPhoneNumber() {
                            return null;
                        }

                        @Override
                        public float getRating() {
                            return 0;
                        }

                        @Override
                        public int getPriceLevel() {
                            return 0;
                        }

                        @Nullable
                        @Override
                        public CharSequence getAttributions() {
                            return null;
                        }

                        @Override
                        public Place freeze() {
                            return null;
                        }

                        @Override
                        public boolean isDataValid() {
                            return false;
                        }
                    };
                    showMarkerDialog(place);
                    return false;
                }
            });
            return m;
        } else {
            Toast.makeText(getActivity(), "You have not searched places", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void zoomToSearchedLocation(final Place place) {
        googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(String.valueOf(place.getAddress())));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                // AlertsUtils.showMarkerDialog(getActivity(),marker.getTitle());
                final String id = mHashMap.get(marker.getId());
                if (id != null) {
                    Place place = new Place() {
                        @Override
                        public String getId() {
                            return id;
                        }

                        @Override
                        public List<Integer> getPlaceTypes() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public CharSequence getAddress() {
                            return marker.getTitle();
                        }

                        @Override
                        public Locale getLocale() {
                            return null;
                        }

                        @Override
                        public CharSequence getName() {
                            return null;
                        }

                        @Override
                        public LatLng getLatLng() {
                            return marker.getPosition();
                        }

                        @Nullable
                        @Override
                        public LatLngBounds getViewport() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Uri getWebsiteUri() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public CharSequence getPhoneNumber() {
                            return null;
                        }

                        @Override
                        public float getRating() {
                            return 0;
                        }

                        @Override
                        public int getPriceLevel() {
                            return 0;
                        }

                        @Nullable
                        @Override
                        public CharSequence getAttributions() {
                            return null;
                        }

                        @Override
                        public Place freeze() {
                            return null;
                        }

                        @Override
                        public boolean isDataValid() {
                            return false;
                        }
                    };
                    showMarkerDialog(place);
                } else {
                    showMarkerDialog(place);
                }
                return true;
            }

        });

    }

    @Override
    public void onLocationChanged(Location location) {
        myCurrentLocation = location;
        AppController.USER_LOCATION_LAT = location.getLatitude();
        AppController.USER_LOCATION_LONG = location.getLongitude();
        if (!hasPoints) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            if (googleMap != null) {
                googleMap.animateCamera(cameraUpdate);
                locationManager.removeUpdates(this);
            }
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

    public void showMarkerDialog(final Place place) {
        final ReviewLocation[] reviewLocation = {null};
        final boolean[] isPlaceExist = new boolean[1];
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("ReviewLocation");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(place.getId())) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        reviewLocation[0] = dataSnapshot1.getValue(ReviewLocation.class);
                    isPlaceExist[0] = true;
                }
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog
                        , null);
                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();
                TextView tvTown = dialogView.findViewById(R.id.tvTownCustomDialog);
                tvTown.setText(place.getAddress());
                Button btnReview = dialogView.findViewById(R.id.btnReviewCustomDialog);
                Button btnExisting = dialogView.findViewById(R.id.btnExistingCustomDialog);
                btnReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (reviewLocation[0] != null) {
                            if (Haversine.distance(place.getLatLng().latitude, place.getLatLng().longitude, myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude()) <= 10) {
                                showReviewDialog(place, reviewLocation[0]);
                            } else {
                                Toast.makeText(getActivity(), "You are too far from the request to join the discussion ", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            showReviewDialog(place);
                        alertDialog.dismiss();
                    }
                });
                btnExisting.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("place_id", place.getId());
                        if (reviewLocation[0] != null) {
                            intent.putExtra("place_name", reviewLocation[0].getLocationName());
                        }
                        getActivity().startActivity(intent);
                    }
                });
                if (!isPlaceExist[0])

                {
                    btnExisting.setEnabled(false);
                }
                alertDialog.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void showReviewDialog(final Place place, final ReviewLocation reviewLocation) {


        final String strTextBox;
        final float value;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_reviewdialog
                , null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView tvTown = dialogView.findViewById(R.id.tvAddress);
        tvTown.setText(place.getAddress());

        Button btnReview = dialogView.findViewById(R.id.btnSubmitCustomDialogReview);
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingbar);
        final EditText etComment = (EditText) dialogView.findViewById(R.id.etTextBox);
        strTextBox = etComment.getText().toString();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float v, boolean b) {
                ratingBar.setRating(v);

            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("ReviewLocation");
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(place.getId())) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("place_id", place.getId());
                            intent.putExtra("place_name", reviewLocation.getLocationName());
                            intent.putExtra("comment", etComment.getText().toString());
                            getActivity().startActivity(intent);
                            FirebaseMessaging.getInstance().subscribeToTopic(place.getId());
                        } else {
                            placeComment(place, etComment.getText().toString(), ratingBar.getRating());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }


    public void showReviewDialog(final Place place) {

        final String strTextBox;
        final float value;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_reviewdialog
                , null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView tvTown = dialogView.findViewById(R.id.tvAddress);
        tvTown.setText(place.getAddress());

        Button btnReview = dialogView.findViewById(R.id.btnSubmitCustomDialogReview);
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingbar);
        final EditText etComment = (EditText) dialogView.findViewById(R.id.etTextBox);
        strTextBox = etComment.getText().toString();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float v, boolean b) {
                ratingBar.setRating(v);

            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("ReviewLocation");
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(place.getId())) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("place_id", place.getId());
                            intent.putExtra("latitude", place.getLatLng().latitude);
                            intent.putExtra("longitude", place.getLatLng().longitude);
                            intent.putExtra("place_name", place.getName());
                            intent.putExtra("comment", etComment.getText().toString());
                            getActivity().startActivity(intent);
                            FirebaseMessaging.getInstance().subscribeToTopic(place.getId());
                        } else {
                            placeComment(place, etComment.getText().toString(), ratingBar.getRating());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void placeComment(final Place place, String comment, float rating) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String Uid = firebaseUser.getUid();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final ReviewLocation reviewLocation = new ReviewLocation(Uid, String.valueOf(place.getAddress()), comment, place.getLatLng().latitude, place.getLatLng().longitude, rating, null);
        database.child("ReviewLocation").child(place.getId()).setValue(reviewLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("usho", "review");
                    FirebaseMessaging.getInstance().subscribeToTopic(place.getId());
                    database.child("user").child(firebaseUser.getUid()).child("groups").child(place.getId()).setValue(reviewLocation);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("wanasho", "review");
                Log.e("Error", e.toString());
            }
        });
    }
}
