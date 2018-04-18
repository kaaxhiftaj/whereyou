package com.techease.whereyou.controllers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;

/**
 * Created by Jarvis on 6/14/2017.
 */

public class AppController extends MultiDexApplication {

    public static double USER_LOCATION_LAT = 0.0;
    public static double USER_LOCATION_LONG = 0.0;
    private static AppController mInstance;

    public static AppController getInstance() {
        return mInstance;
    }


    public static AppController create(Context context) {
        return AppController.get(context);
    }

    private static AppController get(Context context) {
        return (AppController) context.getApplicationContext();
    }

    public static AppController context() {
        return mInstance;
    }

    public static Resources resources() {
        return getInstance().getResources();
    }

    public static int color(int resId) {
        return ContextCompat.getColor(getInstance(), resId);
    }

    public static Drawable drawable(int resId) {
        return ContextCompat.getDrawable(getInstance(), resId);
    }

    public static String string(int resId) {
        return resources().getString(resId);
    }

    public static String string(int resId, Object... args) {
        return resources().getString(resId, args);
    }

    public static String[] stringArray(int resId) {
        return resources().getStringArray(resId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(mInstance);
    }
}
