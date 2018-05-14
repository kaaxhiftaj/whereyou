package com.techease.whereyou.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.adapters.MyReviewsAdapter;
import com.techease.whereyou.ui.models.ReviewLocation;
import com.techease.whereyou.utils.AlertsUtils;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment {


    RecyclerView recyclerView;
    MyReviewsAdapter myReviewsAdapter;
    ArrayList<ReviewLocation> reviewLocations;
    android.support.v7.app.AlertDialog alertDialog;
    FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reviews, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.rvReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewLocations = new ArrayList<>();
        myReviewsAdapter = new MyReviewsAdapter(getActivity(), reviewLocations);
        recyclerView.setAdapter(myReviewsAdapter);

        if (alertDialog == null)
            alertDialog = AlertsUtils.createProgressDialog(getActivity());


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("ReviewLocation");
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("userId").getValue().equals(mAuth.getUid())) {
                        ReviewLocation reviewLocation = dataSnapshot1.getValue(ReviewLocation.class);
                        reviewLocation.setReviewId(dataSnapshot1.getKey());
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        reviewLocations.add(reviewLocation);
                        myReviewsAdapter.notifyDataSetChanged();
                    }
                }
                if (reviewLocations.size() < 1) {
                    Toast.makeText(getActivity(), "You haven't reviewed any place yet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("REVIEWS");
    }
}
