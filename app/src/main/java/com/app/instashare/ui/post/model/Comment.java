package com.app.instashare.ui.post.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.instashare.ui.user.model.UserBasic;
import com.google.firebase.database.Exclude;

/**
 * Created by Pitisflow on 30/5/18.
 */

public class Comment implements Parcelable {

    private UserBasic user;
    private String commentText;
    private String postKey;
    private String commentKey;
    private String audioURL;
    private boolean isNew;
    private long timestamp;


    public Comment() {

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

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Exclude
    public boolean isNew() {
        return isNew;
    }

    @Exclude
    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Exclude
    public String getCommentKey() {
        return commentKey;
    }

    @Exclude
    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.commentText);
        dest.writeString(this.postKey);
        dest.writeString(this.commentKey);
        dest.writeString(this.audioURL);
        dest.writeLong(this.timestamp);
    }

    protected Comment(Parcel in) {
        this.user = in.readParcelable(UserBasic.class.getClassLoader());
        this.commentText = in.readString();
        this.postKey = in.readString();
        this.commentKey = in.readString();
        this.audioURL = in.readString();
        this.timestamp = in.readLong();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
