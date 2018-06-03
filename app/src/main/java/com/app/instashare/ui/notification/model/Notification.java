package com.app.instashare.ui.notification.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.instashare.ui.user.model.UserBasic;
import com.google.firebase.database.Exclude;

/**
 * Created by Pitisflow on 3/6/18.
 */

public class Notification implements Parcelable {

    private UserBasic user;
    private String postKey;
    private String type;
    private long timestamp;
    private String notificationKey;
    private boolean isRead;


    public Notification() {
    }


    public UserBasic getUser() {
        return user;
    }

    public void setUser(UserBasic user) {
        this.user = user;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public String getNotificationKey() {
        return notificationKey;
    }

    @Exclude
    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.postKey);
        dest.writeString(this.type);
        dest.writeLong(this.timestamp);
        dest.writeString(this.notificationKey);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
    }

    protected Notification(Parcel in) {
        this.user = in.readParcelable(UserBasic.class.getClassLoader());
        this.postKey = in.readString();
        this.type = in.readString();
        this.timestamp = in.readLong();
        this.notificationKey = in.readString();
        this.isRead = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel source) {
            return new Notification(source);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}
