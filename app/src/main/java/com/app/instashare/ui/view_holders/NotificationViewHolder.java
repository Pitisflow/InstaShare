package com.app.instashare.ui.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.app.instashare.R;
import com.app.instashare.adapter.NotificationRVAdapter;
import com.app.instashare.ui.notification.model.Notification;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.DateUtils;
import com.app.instashare.utils.Utils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.Util;

/**
 * Created by Pitisflow on 3/6/18.
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView userImage;
    private TextView notificationText;
    private TextView notificationTimestamp;

    public NotificationViewHolder(View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.userImage);
        notificationText = itemView.findViewById(R.id.text);
        notificationTimestamp = itemView.findViewById(R.id.timestamp);

        notificationText.setMovementMethod(LinkMovementMethod.getInstance());
    }



    public void bind(Notification notification, Context context, NotificationRVAdapter.OnNotificationClick listener)
    {
        Picasso.get().load(notification.getUser().getMainImage())
                .resize(userImage.getLayoutParams().width, userImage.getLayoutParams().height)
                .placeholder(R.mipmap.ic_launcher)
                .into(userImage);

        userImage.setOnClickListener(view -> listener.onUserClicked(notification));


        SpannableString username = Utils.getSpannableFromString(notification.getUser().getUsername(),
                context.getResources().getColor(R.color.colorPrimary), false, true,
                () -> listener.onUserClicked(notification));


        SpannableString post = null;
        if (!notification.getType().equals(Constants.NOTIFICATION_TYPE_FOLLOW)) {
            post = Utils.getSpannableFromString(context.getString(R.string.notification_post),
                    context.getResources().getColor(R.color.colorPrimary), false, true,
                    () -> listener.onPostClicked(notification));
        }



        if (notification.getType().equals(Constants.NOTIFICATION_TYPE_COMMENT)) {
            notificationText.setText(TextUtils.concat(username, " ",
                    context.getString(R.string.notification_commented), " ", post));


        } else if (notification.getType().equals(Constants.NOTIFICATION_TYPE_LIKE)) {
            notificationText.setText(TextUtils.concat(username, " ",
                    context.getString(R.string.notification_commented), " ", post));


        } else if (notification.getType().equals(Constants.NOTIFICATION_TYPE_SHARE)) {
            notificationText.setText(TextUtils.concat(username, " ",
                    context.getString(R.string.notification_commented), " ", post));


        } else {
            notificationText.setText(TextUtils.concat(username, " ",
                    context.getString(R.string.notification_follow)));
        }


        notificationTimestamp.setText(DateUtils.getPostDateFromTimestamp(notification.getTimestamp(), context));


        if (!notification.isRead()) itemView.setBackgroundColor(context.getResources().getColor(R.color.notificationColor));
        else itemView.setBackgroundColor(context.getResources().getColor(R.color.white));


        if (!notification.getType().equals(Constants.NOTIFICATION_TYPE_FOLLOW)) {
            itemView.setOnClickListener(view -> listener.onPostClicked(notification));
        } else itemView.setOnClickListener(view -> listener.onUserClicked(notification));


        itemView.setOnLongClickListener(view -> {
            listener.onLongClicked(notification);
            return false;
        });
    }


}
