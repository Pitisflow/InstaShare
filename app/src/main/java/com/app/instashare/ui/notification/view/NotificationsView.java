package com.app.instashare.ui.notification.view;

import com.app.instashare.ui.notification.model.Notification;

/**
 * Created by Pitisflow on 3/6/18.
 */

public interface NotificationsView {
    void addNotification(Notification notification);

    void removeNotification(Notification notification);

    void modifyNotification(Notification oldCard, Notification newNotification);
}
