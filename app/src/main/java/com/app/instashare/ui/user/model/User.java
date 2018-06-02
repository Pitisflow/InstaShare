package com.app.instashare.ui.user.model;

import java.util.Map;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class User {

    private UserBasic basicInfo;
    private Map<String, Object> information;
    private Map<String, Object> social;
    private Map<String, Object> location;
    private Map<String, Object> privacy;


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

    public Map<String, Object> getSocial() {
        return social;
    }

    public void setSocial(Map<String, Object> social) {
        this.social = social;
    }

    public Map<String, Object> getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Map<String, Object> privacy) {
        this.privacy = privacy;
    }
}
