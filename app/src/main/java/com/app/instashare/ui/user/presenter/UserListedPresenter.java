package com.app.instashare.ui.user.presenter;

import android.content.Context;
import android.os.Parcelable;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.post.model.Comment;
import com.app.instashare.ui.user.model.UserBasic;
import com.app.instashare.ui.user.view.UserListedView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pitisflow on 2/6/18.
 */

public class UserListedPresenter implements UserInteractor.OnUserListFetched{

    private Context context;
    private UserListedView view;


    private ArrayList<UserBasic> currentUsers;
    private ArrayList<UserBasic> temp;
    private String typeList;
    private String userKey;
    private AtomicInteger control = new AtomicInteger();


    public UserListedPresenter(Context context, UserListedView view) {
        this.context = context;
        this.view = view;
    }


    public void onInitialize(ArrayList<Parcelable> users, String typeList, String userKey)
    {
        this.typeList = typeList;
        this.userKey = userKey;

        currentUsers = new ArrayList<>();

        System.out.println("HER123124E");
        if (users != null)
        {
            for (Parcelable user : users)
            {
                if (user instanceof UserBasic) currentUsers.add((UserBasic) user);
            }
        }

        System.out.println("HER123124E  " + currentUsers.size());

        if (currentUsers.size() == 0)
        {
            UserInteractor.downloadUsersFromList(typeList, this.userKey, this);
        }
    }

    public void terminate()
    {
        view = null;
    }


    public void onDownloadMoreUsers()
    {
        //DOWNLOAD MORE
    }


    //********************************************
    //IMPLEMENTING IMPLEMENTING USER INTERACTOR
    //********************************************

    @Override
    public void onSearchRefreshed() {
        view.setIsLoading(true);
    }

    @Override
    public void onUserBasicFetched(UserBasic user) {
        control.getAndSet(control.decrementAndGet());
        if (control.intValue() == 0 && view != null)
        {
            temp.add(user);
            currentUsers.addAll(temp);

            view.setIsLoading(false);
            for (UserBasic u : temp) {
                view.addUser(u);
            }
        } else temp.add(user);
    }

    @Override
    public void numOfUsers(int num) {
        control.getAndSet(num);
        temp = new ArrayList<>();

        if (num == 0) view.setIsLoading(false);
    }
}
