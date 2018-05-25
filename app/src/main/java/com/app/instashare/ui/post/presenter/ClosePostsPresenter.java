package com.app.instashare.ui.post.presenter;

import android.content.Context;

import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.ClosePostsView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Pitisflow on 24/5/18.
 */

public class ClosePostsPresenter implements UserData.OnUserDataFetched, PostInteractor.OnDowloadingPosts {

    private Context context;
    private ClosePostsView view;

    private Map<String, Object> location;


    public ClosePostsPresenter(Context context, ClosePostsView view) {
        this.context = context;
        this.view = view;
    }



    public void onInitialize()
    {
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null)
        {
            location = UserData.getUser().getLocation();
            PostInteractor.getClosestPosts(1, UserData.getUser().getLocation(), this);
        }else UserData.addListener(this);
    }

    public void terminate()
    {
        view = null;
        UserData.removeListener(this);
    }



    public void refreshRecycler()
    {

    }




    @Override
    public void updateUserInfo() {
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null)
        {
            location = UserData.getUser().getLocation();
            PostInteractor.getClosestPosts(1, UserData.getUser().getLocation(), this);
            UserData.removeListener(this);
        }
    }



    @Override
    public void downloading() {
        view.enableLoadingView(true);
    }

    @Override
    public void downloadCompleted(ArrayList<Post> posts) {
        view.enableLoadingView(false);
        for (Post post : posts)
        {
            view.addPost(post);
        }
    }
}
