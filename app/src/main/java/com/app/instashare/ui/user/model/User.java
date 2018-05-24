package com.app.instashare.ui.user.model;

import java.util.Map;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class User {

    private UserBasic basicInfo;
    private Map<String, Object> information;
    private Map<String, Object> location;


    public User() {}




    public UserBasic getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(UserBasic basicInfo) {
        this.basicInfo = basicInfo;
    }

    public Map<String, Object> getInformation() {
        return information;
    }

    public void setInformation(Map<String, Object> information) {
        this.information = information;
    }

    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }
}
