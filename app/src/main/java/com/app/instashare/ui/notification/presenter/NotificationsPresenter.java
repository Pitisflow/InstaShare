package com.app.instashare.ui.notification.presenter;

import com.app.instashare.interactor.NotificationInteractor;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.notification.model.Notification;
import com.app.instashare.ui.notification.view.NotificationsView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Pitisflow on 3/6/18.
 */

public class NotificationsPresenter implements UserData.OnNotificationsFetched{

    private NotificationsView view;
    private ArrayList<Notification> currentNotifications = new ArrayList<>();



    public NotificationsPresenter(NotificationsView view) {
        this.view = view;
    }


    public void onInitialize()
    {
        UserData.addNotificationsListener(this);

        if (UserData.getNotifications() != null) {
            for (Notification notification : new ArrayList<>(UserData.getNotifications().values())) {
                currentNotifications.add(notification);
                view.addNotification(notification);
            }
        }
    }



    public void terminate()
    {
        view = null;
        UserData.removeNotificationsListener(this);
    }


    public void setNotificationRead(Notification notification)
    {
        if (UserInteractor.getUserKey() != null) NotificationInteractor.setNotificationRead(notification, UserInteractor.getUserKey());
    }

    public void removeNotification(Notification notification)
    {
        if (notification != null && UserInteractor.getUserKey() != null) {
            NotificationInteractor.removeNotification(notification, UserInteractor.getUserKey());
        }
    }



    @Override
    public void notificationAdded(Notification notification) {
        view.addNotification(notification);
        currentNotifications.add(notification);
    }

    @Override
    public void notificationRemoved(Notification notification) {
        for (Notification not : currentNotifications) {
            if (notification.getNotificationKey().equals(not.getNotificationKey())) {
                 view.removeNotification(not);
            }
        }
    }

    @Override
    public void notificationChanged(Notification notification) {
        for (Notification not : currentNotifications) {
            if (notification.getNotificationKey().equals(not.getNotificationKey())) {
                view.modifyNotification(not, notification);
            }
        }
    }
}
