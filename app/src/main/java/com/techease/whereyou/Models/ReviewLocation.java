package com.techease.whereyou.Models;

/**
 * Created by Adam Noor on 02-Feb-18.
 */

public class ReviewLocation {
    private String userId, locationName, comment;
    private double ratValue;
    private latLng latLng;


    public ReviewLocation() {

    }

    public ReviewLocation(String userId, String locationName, String comment, latLng latLng, float ratValue) {
        this.userId = userId;
        this.comment = comment;
        this.locationName = locationName;
        this.latLng = latLng;
        this.ratValue = ratValue;

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRatValue() {
        return ratValue;
    }

    public void setRatValue(double ratValue) {
        this.ratValue = ratValue;
    }

    public com.techease.whereyou.Models.latLng getLatLng() {
        return latLng;
    }

    public void setLatLng(com.techease.whereyou.Models.latLng latLng) {
        this.latLng = latLng;
    }
}
