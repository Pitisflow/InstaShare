package com.app.instashare.ui.base.presenter;

import android.content.Context;
import android.location.Location;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.base.view.MainView;
import com.app.instashare.ui.notification.model.Notification;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.utils.Constants;

/**
 * Created by Pitisflow on 20/4/18.
 */

public class MainPresenter implements UserData.OnUserDataFetched, UserData.OnNotificationsFetched{

    private Context context;
    private MainView view;


    public MainPresenter(Context context, MainView view) {
        this.context = context;
        this.view = view;
    }


    public void onInitialize()
    {
        UserData.getInstance(this);
        UserData.addListener(this);
        UserData.addNotificationsListener(this);
        if (view.getNavigationText().equals("0") || view.getNavigationText().length() == 0) view.hideNotificationsNumber();

        if (UserData.getUser() != null) showUserInfo();
    }

    public void setCurrentLocation(Location location)
    {
        UserInteractor.updateUserLocation(location);
    }

    public void terminate()
    {
        this.view = null;
        UserData.removeListener(this);
        UserData.removeNotificationsListener(this);
    }


    private void showUserInfo()
    {
        if (UserData.getUser().getInformation().containsKey(Constants.USER_BACKGROUND_K)){
            view.setBackgroundImage((String) UserData.getUser().getInformation().get(Constants.USER_BACKGROUND_K));
        } else view.setBackgroundImage("https://upload.wikimedia.org/wikipedia/commons/d/d1/Mount_Everest_as_seen_from_Drukair2_PLW_edit.jpg");

        if (UserData.getUser().getBasicInfo().getName() != null) view.setName(UserData.getUser().getBasicInfo().getName());
        if (UserData.getUser().getBasicInfo().getUsername() != null) view.setUsername(UserData.getUser().getBasicInfo().getUsername());
        if (UserData.getUser().getBasicInfo().getMainImage() != null) view.setUserImage(UserData.getUser().getBasicInfo().getMainImage());
    }

    @Override
    public void updateUserInfo() {
        if (UserData.getUser() != null) showUserInfo();
    }


    @Override
    public void notificationAdded(Notification notification) {
        if (view != null)
        {
            String text = view.getNavigationText();

            if (text.equals("") && !notification.isRead()) view.updateNotificationsIcon(1);
            else if(!notification.isRead()) view.updateNotificationsIcon(Integer.parseInt(text) + 1);
        }
    }

    @Override
    public void notificationRemoved(Notification notification) {
        if (view != null)
        {
            String text = view.getNavigationText();

            if (text.equals("")) view.updateNotificationsIcon(1);
            else view.updateNotificationsIcon(Integer.parseInt(text) - 1);
        }
    }

    @Override
    public void notificationChanged(Notification notification) {
        String text = view.getNavigationText();
        if (!notification.isRead()) view.updateNotificationsIcon(Integer.parseInt(text) - 1);
    }
}
