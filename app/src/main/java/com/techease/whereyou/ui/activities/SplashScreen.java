package com.techease.whereyou.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.techease.whereyou.FirebaseClasses.MyFirebaseInstanceIDService;
import com.techease.whereyou.R;
import com.techease.whereyou.utils.Configuration;
import com.techease.whereyou.utils.Constants;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String user_id;
    Handler handler;

    public static final String TAG = SplashScreen.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = SplashScreen.this.getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        user_id = sharedPreferences.getString("user_id", "");
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        handler = new Handler();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPlayServices()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(sharedPreferences.getString(Constants.FIREBASE_FCM_TOKEN, ""))) {
                        MyFirebaseInstanceIDService firebaseIntent = new MyFirebaseInstanceIDService();
                        firebaseIntent.onTokenRefresh();
                    }
                }
            });
        }
        handler.postDelayed(new Runnable() {
            public void run() {
                check();
            }
        }, 2000L);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }

    public void check() {
        if (user_id.equals("")) {
            handler = null;
            startActivity(new Intent(SplashScreen.this, FullScreenActivity.class));
            finish();
        } else {
            handler = null;
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }
    }
}
