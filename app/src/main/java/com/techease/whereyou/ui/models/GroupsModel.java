package com.techease.whereyou.ui.models;

/**
 * Created by Adam Noor on 14-Feb-18.
 */

public class GroupsModel {
    private String GroupName;
    private double ratingValue;
    private String groupId;


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
}
