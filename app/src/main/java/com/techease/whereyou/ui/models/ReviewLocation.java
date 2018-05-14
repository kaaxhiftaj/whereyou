package com.techease.whereyou.ui.models;

/**
 * Created by Adam Noor on 02-Feb-18.
 */

public class ReviewLocation {
    double lat, lon;
    private String userId, locationName, comment;
    private double ratValue;
    private String reviewId;


    public ReviewLocation() {
    }

    public ReviewLocation(String userId, String locationName, String comment, double lat, double lon, float ratValue, String reviewId) {
        this.userId = userId;
        this.comment = comment;
        this.locationName = locationName;
        this.lat = lat;
        this.lon = lon;
        this.ratValue = ratValue;
        this.reviewId = reviewId;


    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
