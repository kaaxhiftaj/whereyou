package com.techease.whereyou.ui.models;

/**
 * Created by Adam Noor on 14-Feb-18.
 */

public class GroupsModel {
    private String GroupName;
    private double ratingValue;
    private String groupId;
    private double latitude;
    private double longitude;

    public GroupsModel() {
    }

    public GroupsModel(String groupName, double ratingValue, String groupId, double latitude, double longitude) {
        GroupName = groupName;
        this.ratingValue = ratingValue;
        this.groupId = groupId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
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
