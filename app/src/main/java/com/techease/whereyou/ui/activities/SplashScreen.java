package com.techease.whereyou.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.techease.whereyou.R;
import com.techease.whereyou.utils.Configuration;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor ;
    String user_id ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = SplashScreen.this.getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        user_id = sharedPreferences.getString("user_id","");
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));



        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    check();
                }
            }
        };
        timer.start();


    }


    public void check(){

        if (user_id.equals("")) {

            startActivity(new Intent(SplashScreen.this, FullScreenActivity.class));
            finish();
        } else {

            startActivity(new Intent(SplashScreen.this , MainActivity.class));
        }
    }
}
