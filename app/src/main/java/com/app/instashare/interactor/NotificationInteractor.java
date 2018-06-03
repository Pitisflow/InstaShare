package com.app.instashare.interactor;

import com.app.instashare.singleton.DatabaseSingleton;
import com.app.instashare.ui.notification.model.Notification;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;

import okhttp3.internal.Util;

/**
 * Created by Pitisflow on 3/6/18.
 */

public class NotificationInteractor {

    public static void sendNotification(UserBasic user, String receiverKey, String postKey, String type)
    {
        String path = Utils.createChild(Constants.NOTIFICATIONS_T, receiverKey);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setPostKey(postKey);
        notification.setType(type);
        notification.setTimestamp(System.currentTimeMillis());


        DatabaseSingleton.getDbInstance().child(path).push().setValue(notification);
    }


    public static void setNotificationRead(Notification notification, String userKey)
    {
        String path = Utils.createChild(Constants.NOTIFICATIONS_T, userKey, notification.getNotificationKey(), Constants.NOTIFICATION_READ_K);
        DatabaseSingleton.getDbInstance().child(path).setValue(true);
    }


    public static void removeNotification(Notification notification, String userKey)
    {
        String path = Utils.createChild(Constants.NOTIFICATIONS_T, userKey, notification.getNotificationKey());
        DatabaseSingleton.getDbInstance().child(path).removeValue();
    }
}
