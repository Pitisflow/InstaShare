package com.app.instashare.ui.post.model;

import com.app.instashare.ui.user.model.UserBasic;

/**
 * Created by Pitisflow on 28/5/18.
 */

public class Report {

    private String postKey;
    private UserBasic user;
    private String report;


    public Report() {
    }


    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public UserBasic getUser() {
        return user;
    }

    public void setUser(UserBasic user) {
        this.user = user;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
