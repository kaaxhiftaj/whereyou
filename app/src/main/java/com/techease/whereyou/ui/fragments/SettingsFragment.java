package com.techease.whereyou.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.models.ReviewLocation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsFragment extends Fragment {

    @BindView(R.id.settingsName)
    TextView name;
    Unbinder unbinder;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        unbinder = ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("user");

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("userId").getValue().equals(mAuth.getUid())) {
                        ReviewLocation reviewLocation = dataSnapshot1.getValue(ReviewLocation.class);
                        String nam = String.valueOf(dataSnapshot1.child("name").getValue());
                        name.setText(nam);

                    }
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
        getActivity().setTitle("SETTINGS");
    }
}


