package com.app.instashare.ui.user.presenter;

import android.content.Context;

import com.app.instashare.R;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.ui.user.view.UserProfileView;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 1/6/18.
 */

public class UserProfilePresenter implements
        UserData.OnUserDataFetched,
        UserInteractor.OnCompleteUserDownload,
        UserInteractor.OnCheckedFollowedUser{


    private Context context;
    private UserProfileView view;


    private User user;
    private String userKey;
    private int followers;
    private boolean isMe = false;
    private String defaultBackgroundURL = "https://upload.wikimedia.org/wikipedia/commons/d/d1/Mount_Everest_as_seen_from_Drukair2_PLW_edit.jpg";


    public UserProfilePresenter(Context context, UserProfileView view) {
        this.context = context;
        this.view = view;
    }


    //********************************************
    //INITIALIZING AND TERMINATING
    //********************************************

    public void onInitialize(String userKey)
    {
        this.userKey = userKey;

        UserInteractor.example();
        UserData.addListener(this);
        if (userKey.equals(UserInteractor.getUserKey()))
        {
            isMe = true;

            if (UserData.getUser() != null) {
                user = UserData.getUser();
                checkPrivacy();
                fillUserView();
            }
        } else {
            UserInteractor.downloadCompleteUser(userKey, this);

            if (UserInteractor.getUserKey() != null) {
                UserInteractor.checkIsFollowed(userKey, UserInteractor.getUserKey(), this);
            }
        }
    }

    public void terminate()
    {
        UserData.removeListener(this);
    }


    //********************************************
    //ACTIVITY ACTIONS
    //********************************************

    public void onFollowPressed()
    {
        if (UserInteractor.getUserKey() != null && userKey != null) {
            UserInteractor.followUser(userKey, UserInteractor.getUserKey(),false);
            view.enableFollowButton(true, true);
            view.setFollowers(followers + 1);
        }
    }


    public void onUnFollowPressed()
    {
        if (UserInteractor.getUserKey() != null && userKey != null) {
            UserInteractor.followUser(userKey, UserInteractor.getUserKey(),true);
            view.enableFollowButton(true, false);
            view.setFollowers(followers - 1);
        }
    }


    public void onUserImagePressed()
    {
        if (user.getBasicInfo().getMainImage() != null) {
            view.openPhotoActivity(user.getBasicInfo().getMainImage());
        }
    }






    //********************************************
    //LOADING DATA
    //********************************************

    private void checkPrivacy()
    {
        Map<String, Object> privacy;

        if (isMe) {
            view.enableFollowButton(false, false);
            view.enableSeeFollowers(true);
            view.enableSeeFollowing(true);
            view.enableSeeMoreImages(true);
            view.enableViewAllPosts(true);
            view.enableEmail(true);
        }
        else {
            if (user.getPrivacy() != null) {
                privacy = user.getPrivacy();

                if (privacy.containsKey(Constants.USER_PRIVACY_FOLLOWERS_K) && !(Boolean) privacy.get(Constants.USER_PRIVACY_FOLLOWERS_K)) {
                    view.enableSeeFollowers(false);
                } else view.enableSeeFollowers(true);

                if (privacy.containsKey(Constants.USER_PRIVACY_FOLLOWINGS_K) && !(Boolean) privacy.get(Constants.USER_PRIVACY_FOLLOWINGS_K)) {
                    view.enableSeeFollowing(false);
                } else view.enableSeeFollowing(true);

                if (privacy.containsKey(Constants.USER_PRIVACY_IMAGES_K) && !(Boolean) privacy.get(Constants.USER_PRIVACY_IMAGES_K)) {
                    view.enableSeeMoreImages(false);
                } else view.enableSeeMoreImages(true);

                if (privacy.containsKey(Constants.USER_PRIVACY_POSTS_K) && !(Boolean) privacy.get(Constants.USER_PRIVACY_POSTS_K)) {
                    view.enableViewAllPosts(false);
                }   view.enableViewAllPosts(true);

                if (privacy.containsKey(Constants.USER_PRIVACY_EMAIL_K) && !(Boolean) privacy.get(Constants.USER_PRIVACY_EMAIL_K)) {
                    view.enableEmail(false);
                }   view.enableEmail(true);
            } else {
                view.enableSeeFollowers(true);
                view.enableSeeFollowing(true);
                view.enableSeeMoreImages(true);
                view.enableViewAllPosts(true);
                view.enableEmail(true);
            }
        }
    }


    private void fillUserView()
    {
        Map<String, Object> information;
        Map<String, Object> social;
        String name;



        view.setUsername(user.getBasicInfo().getUsername());
        view.setUserImageView(user.getBasicInfo().getMainImage());
        if (user.getInformation() != null){
            information = user.getInformation();

            if (information.containsKey(Constants.USER_NAME_K) && information.containsKey(Constants.USER_LAST_NAME_K)) {
                name = information.get(Constants.USER_NAME_K) + " " + information.get(Constants.USER_LAST_NAME_K);
            } else if (information.containsKey(Constants.USER_NAME_K)) name = (String) information.get(Constants.USER_NAME_K);
            else if (information.containsKey(Constants.USER_LAST_NAME_K)) name = (String) information.get(Constants.USER_LAST_NAME_K);
            else name = null;

            view.setName(name);
            view.setCompletedUser(user.getBasicInfo().getUsername(), name);


            if (information.containsKey(Constants.USER_BIRTHDATE_K)) {
                view.setYears(DateUtils.getYearsFromStringDate((String) information.get(Constants.USER_BIRTHDATE_K), context));
            } else view.enableYears(false);

            if (information.containsKey(Constants.USER_DESCRIPTION_K)) {
                view.setDescription((String) information.get(Constants.USER_DESCRIPTION_K));
            } else view.setDescription(null);

            if (information.containsKey(Constants.USER_BACKGROUND_K)) {
                view.setUserBackgroundImage((String) information.get(Constants.USER_BACKGROUND_K));
            } else view.setUserBackgroundImage(defaultBackgroundURL);

            if (information.containsKey(Constants.USER_EMAIL_K)) {
                view.setEmail((String) information.get(Constants.USER_EMAIL_K));
            } else view.setEmail(null);
        } else {
            view.setUserBackgroundImage(defaultBackgroundURL);
            view.enableYears(false);
            view.setName(null);
            view.setCompletedUser(user.getBasicInfo().getUsername(), null);
            view.setDescription(null);
        }



        if (user.getSocial() != null)
        {
            social = user.getSocial();

            if (social.containsKey(Constants.USER_POSTS_SHARED_K)) {
                view.setPostsShared(((Long) social.get(Constants.USER_POSTS_SHARED_K)).intValue());
            } else view.setPostsShared(0);

            if (social.containsKey(Constants.USER_FOLLOWERS_K)) {
                followers = ((Long) social.get(Constants.USER_FOLLOWERS_K)).intValue();
                view.setFollowers(((Long) social.get(Constants.USER_FOLLOWERS_K)).intValue());
            } else {
                followers = 0;
                view.setFollowers(0);
            }

            if (social.containsKey(Constants.USER_FOLLOWING_K)) {
                view.setFollowing(((Long) social.get(Constants.USER_FOLLOWING_K)).intValue());
            } else view.setFollowing(0);
        } else {
            view.setFollowing(0);
            view.setFollowers(0);
            view.setPostsShared(0);
        }
    }





    private boolean checkLocationDifferent(Map<String, Object> location1, Map<String, Object> location2)
    {
        if (!location1.get(Constants.USER_LATITUDE_K).equals(location2.get(Constants.USER_LATITUDE_K)) ||
                !location1.get(Constants.USER_LONGITUDE_K).equals(location2.get(Constants.USER_LONGITUDE_K)))
        {
            return false;
        } else return true;
    }


    //********************************************
    //IMPLEMENTING USER DATA
    //********************************************

    @Override
    public void updateUserInfo() {

        if (userKey.equals(UserInteractor.getUserKey()) && (user == null || (UserData.getUser() != null &&
                checkLocationDifferent(UserData.getUser().getLocation(), user.getLocation()))))
        {
            user = UserData.getUser();
            isMe = true;

            checkPrivacy();
            fillUserView();
        }
    }



    //********************************************
    //IMPLEMENTING USER INTERACTOR (user download)
    //********************************************

    @Override
    public void donwloading() {
        //Something
    }

    @Override
    public void downloadSuccessful(User user) {
        this.user = user;

        checkPrivacy();
        fillUserView();
    }

    @Override
    public void downloadFailed() {
        //Something
    }


    //*************************************************
    //IMPLEMENTING USER INTERACTOR (checking followers)
    //*************************************************

    @Override
    public void isFollowed() {
        view.enableFollowButton(true, true);
    }

    @Override
    public void isNotFollowed() {
        view.enableFollowButton(true, false);
    }
}
