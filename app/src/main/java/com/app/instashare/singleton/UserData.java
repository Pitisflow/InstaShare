package com.app.instashare.singleton;

import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 24/4/18.
 */

public class UserData {


    private static User user;
    private static UserData instance;

    private static ArrayList<String> hiddenPosts = new ArrayList<>();
    private static ArrayList<OnUserDataFetched> listeners = new ArrayList<>();



    public static UserData getInstance()
    {
        if (instance == null)
        {
            instance = new UserData();
            instance.fetchUserData();
        }

        return instance;
    }


    public static UserData getInstance(OnUserDataFetched listener)
    {
        addListener(listener);

        if (instance == null)
        {
            instance = new UserData();
            instance.fetchUserData();
            instance.fetchHidePosts();
        }

        return instance;
    }



    public static void addListener(OnUserDataFetched onUserDataFetched) {
        if (!listeners.contains(onUserDataFetched)) {
            listeners.add(onUserDataFetched);
        }
    }

    public static void removeListener(OnUserDataFetched onUserDataFetched) {
        listeners.remove(onUserDataFetched);
    }

    private void notifyUserDataChanged() {
        for (OnUserDataFetched listener : listeners) {
            listener.updateUserInfo();
        }
    }



    private void fetchUserData()
    {
        String path = Utils.createChild(Constants.USERS_T, UserInteractor.getUserKey());

        DatabaseSingleton.getDbInstance().child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    User u = dataSnapshot.getValue(User.class);

                    if (u != null) {
                        user = u;
                        user.getBasicInfo().setUserKey(UserInteractor.getUserKey());
                        notifyUserDataChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void fetchHidePosts()
    {
        String path = Utils.createChild(Constants.POSTS_HIDDEN_T, UserInteractor.getUserKey());

        DatabaseSingleton.getDbInstance().child(path).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    hiddenPosts.add(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hiddenPosts.remove(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static User getUser() {
        return user;
    }

    public static ArrayList<String> getHiddenPosts() {
        return hiddenPosts;
    }

    public interface OnUserDataFetched {
        void updateUserInfo();
    }
}
