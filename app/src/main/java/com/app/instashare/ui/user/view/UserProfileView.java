package com.app.instashare.ui.user.view;

/**
 * Created by Pitisflow on 1/6/18.
 */

public interface UserProfileView {

    void enableSeeFollowers(boolean enabled);

    void enableSeeFollowing(boolean enabled);

    void enableFollowButton(boolean enableButton, boolean isFollowing);

    void enableSeeMoreImages(boolean enabled);

    void enableViewAllPosts(boolean enabled);

    void enableEmail(boolean enabled);

    void enableYears(boolean enabled);

    void setUsername(String username);

    void setName(String name);

    void setPostsShared(int postsShared);

    void setFollowing(int following);

    void setFollowers(int followers);

    void setCompletedUser(String userName, String name);

    void setYears(String years);

    void setDescription(String description);

    void setEmail(String email);

    void setUserImageView(String imageURL);

    void setUserBackgroundImage(String backgroundImage);

    void setNoImagesText(String text);

    void setNoPostsText(String text);

    void addImage(String imageURL);

    void openPhotoActivity(String imageURL);
}
