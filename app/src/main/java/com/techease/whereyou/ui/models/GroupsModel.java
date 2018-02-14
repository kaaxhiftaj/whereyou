package com.techease.whereyou.ui.models;

/**
 * Created by Adam Noor on 14-Feb-18.
 */

public class GroupsModel {
    private String GroupName;
    private double ratingValue;

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }
}
