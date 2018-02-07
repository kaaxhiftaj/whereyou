package com.techease.whereyou.Models;

/**
 * Created by k.zahid on 2/7/18.
 */

public class latLng {

    private double latitude;
    private double longitude;


    public latLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
