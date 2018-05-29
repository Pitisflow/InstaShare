package com.app.instashare.utils;

/**
 * Created by Pitisflow on 16/4/18.
 */

public class Constants {

    //********************************************
    //Card type IDs
    //********************************************
    public static final int CARD_USER_BASIC = 10001;
    public static final int CARD_POST = 20001;
    public static final int CARD_POST_TAG = 20002;
    public static final int CARD_LOADING = 99999;




    //********************************************
    //Card type IDs
    //********************************************
    public static final int CODE_COMES_FROM_ACTIVITY = 1000;



    //********************************************
    //Shared Preferences
    //********************************************
    public static final String PREFERENCES_RADIUS = "radius";




    //********************************************
    //Firebase Trees (T) and Keys (K).
    //********************************************



    //User and Usernames tree keys
    public static final String USERNAMES_T = "usernames";
    public static final String USERS_T = "users";
    public static final String USERS_BASIC_INFO_T = "basicInfo";
    public static final String USERS_INFO_T = "information";
    public static final String USERS_LOCACATION_T = "location";


    public static final String USERNAME_K = "username";
    public static final String USER_EMAIL_K = "email";
    public static final String USER_NAME_K = "name";
    public static final String USER_LAST_NAME_K = "lastName";
    public static final String USER_FIRST_TIMESTAMP_K = "registrationTimestamp";
    public static final String USER_BIRTHDATE_K = "birthdate";
    public static final String USER_MAIN_IMAGE_K = "mainImage";
    public static final String USERNAMES_USERKEY_K = "userKey";
    public static final String USER_LATITUDE_K = "latitude";
    public static final String USER_LONGITUDE_K = "longitude";


    public static final String USER_IMAGES_T = "users-images";
    public static final String USER_IMAGES_NAME_T = "name";




    //Tags tree keys
    public static final String TAG_TRAVEL_K = "travel";
    public static final String TAG_SPORT_K = "sport";
    public static final String TAG_ENTERTAIMENT_K = "entertaiment";
    public static final String TAG_GROUPS_K = "groups";
    public static final String TAG_PHOTO_K = "photo";
    public static final String TAG_VIDEO_K = "video";
    public static final String TAG_CURIOSITIES_K = "curiosities";
    public static final String TAG_PLACES_K = "places";
    public static final String TAG_PARTY_K = "party";
    public static final String TAG_LIFESTYLE_K = "lifestyle";
    public static final String TAG_MEETINGS_K = "meetings";
    public static final String TAG_WORK_K = "work";
    public static final String TAG_FOOD_K = "food";
    public static final String TAG_POLITIC_K = "politic";
    public static final String TAG_SOCIAL_K = "social";
    public static final String TAG_ANIMALS_K = "animals";




    //Post keys tree keys
    public static final String POSTS_T = "posts";
    public static final String POST_KEY_K = "postKey";
    public static final String POST_CONTENT_TEXT_K = "contentText";
    public static final String POST_CONTENT_IMAGE_K = "mediaURL";
    public static final String POST_USER_K = "user";
    public static final String POST_TIMESTAMP_K = "timestamp";
    public static final String POST_TYPE_K = "type";
    public static final String POST_ALIGNUP_K = "isAlignUp";
    public static final String POST_ANONYMOUS_K = "anonymous";
    public static final String POST_PUBLIC_K = "forAll";
    public static final String POST_TAGS_K = "tags";
    public static final String POST_LIKES_K = "numLikes";
    public static final String POST_COMMENTS_K = "numComments";
    public static final String POST_SHARES_K = "numShares";


    public static final String POSTS_LIKED_T = "posts-liked";
    public static final String POSTS_SHARED_T = "posts-shared";
    public static final String POSTS_FAVORITES_T = "posts-favs";
    public static final String POSTS_SAVED_T = "posts-saved";
    public static final String POSTS_REPORTED_T = "posts-reported";
    public static final String POSTS_HIDED_T = "posts-hided";


    //Report tree keys
    public static final String POST_REPORT_K = "report";





    //General keys
    public static final String GENERAL_IMAGES = "images";
    public static final String GENERAL_LOCATION_K = "location";
}
