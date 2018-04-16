package com.app.instashare.ui.user.model;

import java.util.Map;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class User {

    private UserBasic basicInfo;
    private Map<String, Object> information;



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
}
