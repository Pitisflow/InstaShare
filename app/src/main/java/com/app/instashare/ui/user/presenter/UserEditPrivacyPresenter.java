package com.app.instashare.ui.user.presenter;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.user.view.UserEditPrivacyView;
import com.app.instashare.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class UserEditPrivacyPresenter {

    private UserEditPrivacyView view;


    private boolean followingState = false;
    private boolean followersState = false;
    private boolean emailState = false;
    private boolean imagesState = false;
    private boolean postsState = false;


    public UserEditPrivacyPresenter(UserEditPrivacyView view) {
        this.view = view;
    }

    public void onInitialize()
    {
        Map<String, Object> privacy;

        if (UserData.getUser() != null && UserData.getUser().getPrivacy() != null)
        {
            privacy = UserData.getUser().getPrivacy();

            if (privacy.containsKey(Constants.USER_PRIVACY_EMAIL_K)) {
                view.currentShowEmailState((boolean) privacy.get(Constants.USER_PRIVACY_EMAIL_K));
            } else view.currentShowEmailState(true);

            if (privacy.containsKey(Constants.USER_PRIVACY_FOLLOWINGS_K)) {
                view.currentShowFollowingState((boolean) privacy.get(Constants.USER_PRIVACY_FOLLOWINGS_K));
            } else view.currentShowFollowingState(true);

            if (privacy.containsKey(Constants.USER_PRIVACY_FOLLOWERS_K)) {
                view.currentShowFollowersState((boolean) privacy.get(Constants.USER_PRIVACY_FOLLOWERS_K));
            } else view.currentShowFollowersState(true);

            if (privacy.containsKey(Constants.USER_PRIVACY_POSTS_K)) {
                view.currentShowPostsState((boolean) privacy.get(Constants.USER_PRIVACY_POSTS_K));
            } else view.currentShowPostsState(true);

            if (privacy.containsKey(Constants.USER_PRIVACY_IMAGES_K)) {
                view.currentShowImagesState((boolean) privacy.get(Constants.USER_PRIVACY_IMAGES_K));
            } else view.currentShowImagesState(true);

        } else {
            view.currentShowEmailState(true);
            view.currentShowFollowersState(true);
            view.currentShowFollowingState(true);
            view.currentShowImagesState(true);
            view.currentShowPostsState(true);
        }
    }

    public void terminate()
    {
        view = null;
    }


    public void onEmailStateChanged(boolean state) {
        emailState = state;
    }

    public void onFollowingStateChanged(boolean state) {
        followingState = state;
    }

    public void onFollowersStateChanged(boolean state) {
        followersState = state;
    }

    public void onImagesStateChanged(boolean state) {
        imagesState = state;
    }

    public void onPostsStatesChanged(boolean state) {
        postsState = state;
    }


    public void updateUserPrivacy()
    {
        Map<String, Object> newPrivacy = new HashMap<>();

        newPrivacy.put(Constants.USER_PRIVACY_FOLLOWERS_K, followersState);
        newPrivacy.put(Constants.USER_PRIVACY_FOLLOWINGS_K, followingState);
        newPrivacy.put(Constants.USER_PRIVACY_EMAIL_K, emailState);
        newPrivacy.put(Constants.USER_PRIVACY_IMAGES_K, imagesState);
        newPrivacy.put(Constants.USER_PRIVACY_POSTS_K, postsState);

        if (UserInteractor.getUserKey() != null) {
            UserInteractor.updateUserPrivacy(UserInteractor.getUserKey(), newPrivacy);
        }
    }
}
