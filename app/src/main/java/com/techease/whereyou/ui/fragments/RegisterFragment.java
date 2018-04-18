package com.techease.whereyou.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.activities.MainActivity;
import com.techease.whereyou.ui.models.User;
import com.techease.whereyou.utils.AlertsUtils;
import com.techease.whereyou.utils.Configuration;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RegisterFragment extends Fragment {

    @BindView(R.id.et_signup_fullname)
    EditText signup_fullname;

    @BindView(R.id.et_signup_email)
    EditText signup_email;

    @BindView(R.id.et_signup_phone)
    EditText signup_phone;

    @BindView(R.id.et_signup_password)
    EditText signup_password;

    @BindView(R.id.signup_next)
    ImageButton signup_next;

    @BindView(R.id.signup_btnFb)
    Button signup_btnFb;
    DatabaseReference mDatabase;
    String email, password, name, mobile;
    android.support.v7.app.AlertDialog alertDialog;
    Unbinder unbinder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        ToggleSwitch toggleSwitch = (ToggleSwitch) v.findViewById(R.id.signuptoggle);
        toggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {

                if (position == 0) {
                    Fragment fragment = new RegisterFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("abc").commit();
                } else if (position == 1) {
                    Fragment fragment = new LoginFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("abc").commit();
                }
            }
        });

        signup_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog == null)
                    alertDialog = AlertsUtils.createProgressDialog(getActivity());
                alertDialog.show();
                email = signup_email.getText().toString();
                password = signup_password.getText().toString();
                name = signup_fullname.getText().toString();
                editor.putString("name", name).commit();
                mobile = signup_phone.getText().toString();
                Signup(email, password);
            }
        });


        return v;
    }

    public void Signup(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            String userid = mAuth.getCurrentUser().getUid();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(signup_fullname.getText().toString()).build();
                            mAuth.getCurrentUser().updateProfile(profileUpdates);
                            editor.putString("user_id", userid).commit();
                            if (alertDialog != null)
                                alertDialog.dismiss();

                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String Uid = firebaseUser.getUid();
                            User use = new User(Uid, name, email, mobile);
                            DatabaseReference current_user_db = mDatabase.child(userid);
                            current_user_db.setValue(use);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            if (alertDialog != null)
                                alertDialog.dismiss();
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
