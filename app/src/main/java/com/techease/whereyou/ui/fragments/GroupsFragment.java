package com.techease.whereyou.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.adapters.GroupsAdapter;
import com.techease.whereyou.ui.models.GroupsModel;
import com.techease.whereyou.ui.models.ReviewLocation;
import com.techease.whereyou.utils.AlertsUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    RecyclerView rvGroups;
    List<GroupsModel> models;
    GroupsAdapter groupsAdapter;
    android.support.v7.app.AlertDialog alertDialog;
    private DatabaseReference mFirebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        rvGroups = (RecyclerView) view.findViewById(R.id.rvGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        models = new ArrayList<>();
        groupsAdapter = new GroupsAdapter(getActivity(), models);
        rvGroups.setAdapter(groupsAdapter);
        if (alertDialog == null)
            alertDialog = AlertsUtils.createProgressDialog(getActivity());
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("ReviewLocation");
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ReviewLocation reviewLocation = dataSnapshot1.getValue(ReviewLocation.class);
                    GroupsModel model = new GroupsModel();
                    model.setGroupName(reviewLocation.getLocationName());
                    model.setRatingValue(reviewLocation.getRatValue());
                    model.setGroupId(dataSnapshot1.getKey());
                    models.add(model);
                    if (alertDialog != null)
                        alertDialog.dismiss();
                }
                groupsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("GROUPS");
    }
}
