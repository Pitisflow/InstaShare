package com.app.instashare.ui.post.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.app.instashare.ui.user.model.UserBasic;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class Post implements Parcelable, Comparable{

    private String postKey;

    //Post user information
    private UserBasic user;
    private Long timestamp;     //Order By


    //Post content information
    private String contentText;
    private String mediaURL;
    private String type;                        //Filter
    private boolean isAlignUp;
    private boolean isAnonymous;                //Filter
    private boolean isForAll;
    private HashMap<String, Double> locationMap;
    private HashMap<String, Boolean> tags;      //Filter
    private GeoPoint location;                  //Filter


    //Post extra information
    private Long numLikes;      //Order By
    private Long numComments;   //Order By
    private Long numShares;     //Order By


    //Post local values
    private boolean isLiked;
    private boolean isShared;




    public Post() {

    }


    public UserBasic getUser() {
        return user;
    }

    public void setUser(UserBasic user) {
        this.user = user;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAlignUp() {
        return isAlignUp;
    }

    public void setAlignUp(boolean alignUp) {
        isAlignUp = alignUp;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public boolean isForAll() {
        return isForAll;
    }

    public void setForAll(boolean forAll) {
        isForAll = forAll;
    }

    @Exclude
    public HashMap<String, Double> getLocationMap() {
        return locationMap;
    }

    @Exclude
    public void setLocationMap(HashMap<String, Double> locationMap) {
        this.locationMap = locationMap;
    }

    public HashMap<String, Boolean> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, Boolean> tags) {
        this.tags = tags;
    }

    public Long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(Long numLikes) {
        this.numLikes = numLikes;
    }

    public Long getNumComments() {
        return numComments;
    }

    public void setNumComments(Long numComments) {
        this.numComments = numComments;
    }

    public Long getNumShares() {
        return numShares;
    }

    public void setNumShares(Long numShares) {
        this.numShares = numShares;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    @Exclude
    public String getPostKey() {
        return postKey;
    }

    @Exclude
    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    @Exclude
    public boolean isLiked() {
        return isLiked;
    }

    @Exclude
    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Exclude
    public boolean isShared() {
        return isShared;
    }

    @Exclude
    public void setShared(boolean shared) {
        isShared = shared;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof Post)
        {
            if (this.location.equals(((Post) o).getLocation())
                    && this.user.getUserKey().equals(((Post) o).user.getUserKey())
                    && Objects.equals(this.timestamp, ((Post) o).getTimestamp())) return 0;
            else return 1;
        } else return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postKey);
        dest.writeParcelable(this.user, flags);
        dest.writeValue(this.timestamp);
        dest.writeString(this.contentText);
        dest.writeString(this.mediaURL);
        dest.writeString(this.type);
        dest.writeByte(this.isAlignUp ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAnonymous ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isForAll ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.locationMap);
        dest.writeSerializable(this.tags);
        dest.writeValue(this.numLikes);
        dest.writeValue(this.numComments);
        dest.writeValue(this.numShares);
        dest.writeByte(this.isLiked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShared ? (byte) 1 : (byte) 0);
    }

    protected Post(Parcel in) {
        this.postKey = in.readString();
        this.user = in.readParcelable(UserBasic.class.getClassLoader());
        this.timestamp = (Long) in.readValue(Long.class.getClassLoader());
        this.contentText = in.readString();
        this.mediaURL = in.readString();
        this.type = in.readString();
        this.isAlignUp = in.readByte() != 0;
        this.isAnonymous = in.readByte() != 0;
        this.isForAll = in.readByte() != 0;
        this.locationMap = (HashMap<String, Double>) in.readSerializable();
        this.tags = (HashMap<String, Boolean>) in.readSerializable();
        this.numLikes = (Long) in.readValue(Long.class.getClassLoader());
        this.numComments = (Long) in.readValue(Long.class.getClassLoader());
        this.numShares = (Long) in.readValue(Long.class.getClassLoader());
        this.isLiked = in.readByte() != 0;
        this.isShared = in.readByte() != 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
