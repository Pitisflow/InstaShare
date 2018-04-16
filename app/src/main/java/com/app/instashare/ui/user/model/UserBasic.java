package com.app.instashare.ui.user.model;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class UserBasic {

    private String mainImage;
    private String username;
    private String userKey;
    private String name;


    public UserBasic() {}


    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
