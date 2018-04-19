package com.app.instashare.ui.user.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class UserBasic implements Parcelable {

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




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mainImage);
        dest.writeString(this.username);
        dest.writeString(this.userKey);
        dest.writeString(this.name);
    }

    protected UserBasic(Parcel in) {
        this.mainImage = in.readString();
        this.username = in.readString();
        this.userKey = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<UserBasic> CREATOR = new Parcelable.Creator<UserBasic>() {
        @Override
        public UserBasic createFromParcel(Parcel source) {
            return new UserBasic(source);
        }

        @Override
        public UserBasic[] newArray(int size) {
            return new UserBasic[size];
        }
    };
}
