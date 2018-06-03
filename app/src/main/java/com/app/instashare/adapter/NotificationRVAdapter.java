package com.app.instashare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;
import com.app.instashare.ui.notification.model.Notification;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.view_holders.NotificationViewHolder;
import com.app.instashare.ui.view_holders.PostViewHolder;

/**
 * Created by Pitisflow on 3/6/18.
 */

public class NotificationRVAdapter extends BaseRVAdapter {

    private Context context;
    private OnNotificationClick listener;



    public NotificationRVAdapter(Context context) {
        super();
        this.context = context;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((NotificationViewHolder) holder).bind((Notification) getItemList().get(position), context, listener);
    }



    public void setListener(OnNotificationClick listener) {
        this.listener = listener;
    }


    public void removeListener() {
        this.listener = null;
    }




    public interface OnNotificationClick
    {
        void onUserClicked(Notification notification);

        void onPostClicked(Notification notification);

        void onLongClicked(Notification notification);
    }
}
