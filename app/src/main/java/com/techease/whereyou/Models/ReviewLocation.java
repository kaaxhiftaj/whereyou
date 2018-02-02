package com.techease.whereyou.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Adam Noor on 02-Feb-18.
 */

public class ReviewLocation {
    String userId,locationName,comment ;
    float ratValue;
LatLng latLng ;



    public ReviewLocation() {

    }

    public ReviewLocation(String userId,  String locationName, String comment ,LatLng lat , float ratValue) {
        this.userId=userId;
        this.comment=comment;
        this.locationName=locationName;
        this.latLng=lat;
        this.ratValue=ratValue;

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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRatValue() {
        return ratValue;
    }

    public void setRatValue(float ratValue) {
        this.ratValue = ratValue;
    }

}
