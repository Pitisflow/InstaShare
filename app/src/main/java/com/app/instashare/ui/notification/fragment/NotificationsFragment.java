package com.app.instashare.ui.notification.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.instashare.R;
import com.app.instashare.adapter.NotificationRVAdapter;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.notification.model.Notification;
import com.app.instashare.ui.notification.presenter.NotificationsPresenter;
import com.app.instashare.ui.notification.view.NotificationsView;
import com.app.instashare.ui.post.activity.DetailPostActivity;
import com.app.instashare.ui.user.activity.UserProfileActivity;
import com.app.instashare.utils.Constants;

/**
 * Created by Pitisflow on 18/4/18.
 */

public class NotificationsFragment extends Fragment implements NotificationsView,
        NotificationRVAdapter.OnNotificationClick{


    private RecyclerView recyclerView;
    private NotificationRVAdapter adapter;


    private NotificationsPresenter presenter;
    private Notification currentNotification;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        presenter = new NotificationsPresenter(this);
        bindRecyclerView(view);
        presenter.onInitialize();
    }


    private void bindRecyclerView(View view)
    {
        recyclerView = view.findViewById(R.id.recycler);

        adapter = new NotificationRVAdapter(getContext());
        adapter.setListener(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Notification notification = new Notification();
        notification.setUser(UserData.getUser().getBasicInfo());
        notification.setTimestamp(1528022560623L);
        notification.setType(Constants.NOTIFICATION_TYPE_FOLLOW);
    }





    //********************************************
    //IMPLEMENTING ON NOTIFICATION CLICK
    //********************************************

    @Override
    public void onUserClicked(Notification notification) {
        presenter.setNotificationRead(notification);

        Intent intent = UserProfileActivity.newInstance(notification.getUser().getUserKey(), getContext());
        startActivity(intent);
    }

    @Override
    public void onPostClicked(Notification notification) {
        presenter.setNotificationRead(notification);

        Intent intent = DetailPostActivity.newInstance(getContext(), notification.getPostKey());
        startActivity(intent);
    }

    @Override
    public void onLongClicked(Notification notification) {
        currentNotification = notification;

        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.notification_remove));
            builder.setMessage(getString(R.string.notification_remove_text));
            builder.setNegativeButton(getString(R.string.notification_cancel), null);
            builder.setPositiveButton(getString(R.string.notification_confirm),
                    (dialogInterface, i) -> presenter.removeNotification(currentNotification));
            builder.show();
        }
    }


    //********************************************
    //IMPLEMENTING VIEW
    //********************************************

    @Override
    public void addNotification(Notification notification) {
        adapter.addCard(notification, 0);
    }

    @Override
    public void removeNotification(Notification notification) {
        adapter.removeCard(notification);
    }

    @Override
    public void modifyNotification(Notification oldNotification, Notification newNotification) {
        adapter.modifyCard(oldNotification, newNotification);
    }
}
