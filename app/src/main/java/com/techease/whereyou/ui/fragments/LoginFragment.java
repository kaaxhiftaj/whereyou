package com.techease.whereyou.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.activities.MainActivity;
import com.techease.whereyou.utils.AlertsUtils;
import com.techease.whereyou.utils.Configuration;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginFragment extends Fragment {


    @BindView(R.id.et_signin_email)
    EditText signin_email;

    @BindView(R.id.et_signin_password)
    EditText signin_password;

    @BindView(R.id.signin_next)
    ImageButton signin_next;

    Unbinder unbinder;
    DatabaseReference mDatabase;
    String email, password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AlertDialog alertDialog;
    @BindView(R.id.tv_forgot_password)
    TextView tvForgotPassword;
    private FirebaseAuth mAuth;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, v);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        ToggleSwitch toggleSwitch = (ToggleSwitch) v.findViewById(R.id.signintoggle);
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
        signin_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = signin_email.getText().toString();
                password = signin_password.getText().toString();
                if (signin_email.length() == 0) {
                    signin_email.setError("Please enter your email address");
                } else if ((!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                    signin_email.setError("Please enter a valid email address");
                } else if (password.length() == 0) {
                    signin_password.setError("Please enter your password");
                } else {
                    if (alertDialog == null)
                        alertDialog = AlertsUtils.createProgressDialog(getActivity());
                    alertDialog.show();
                    Signin(email, password);
                }
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signin_email.length() == 0) {
                    signin_email.setError("Please enter your email address");
                } else if ((!android.util.Patterns.EMAIL_ADDRESS.matcher(signin_email.getText().toString()).matches())) {
                    signin_email.setError("Please enter a valid email address");
                } else {
                    if (alertDialog == null)
                        alertDialog = AlertsUtils.createProgressDialog(getActivity());
                    alertDialog.show();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(signin_email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (alertDialog != null)
                                        alertDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setMessage("We have sent you an email containing a link to reset your password.");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        final AlertDialog dialog1 = builder.create();
                                        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                                            }
                                        });
                                        dialog1.show();
                                    } else {
                                        Toast.makeText(getActivity(), "No such email found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        return v;
    }


    public void Signin(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final String userId = mAuth.getCurrentUser().getUid();
                            editor.putString("user_id", userId).commit();
                            if (alertDialog != null)
                                alertDialog.dismiss();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        } else {

                            if (alertDialog != null)
                                alertDialog.dismiss();
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
