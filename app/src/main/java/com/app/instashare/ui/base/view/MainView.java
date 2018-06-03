package com.app.instashare.ui.base.view;

/**
 * Created by Pitisflow on 20/4/18.
 */

public interface MainView {

    void setUserImage(String imageURL);

    void setBackgroundImage(String imageURL);

    void setUsername(String username);

    void setName(String name);

    void updateNotificationsIcon(int num);

    void hideNotificationsNumber();

    String getNavigationText();
}
