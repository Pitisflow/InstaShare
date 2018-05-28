package com.app.instashare.ui.post.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.ClosePostsView;
import com.app.instashare.ui.user.model.User;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.LocationUtils;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Pitisflow on 24/5/18.
 */

public class ClosePostsPresenter implements UserData.OnUserDataFetched, PostInteractor.OnDowloadingPosts {

    private Context context;
    private ClosePostsView view;


    private ArrayList<Post> currentPosts;
    private Map<String, Object> location;
    private boolean locationUpdated = false;


    public ClosePostsPresenter(Context context, ClosePostsView view) {
        this.context = context;
        this.view = view;
    }



    public void onInitialize(ArrayList<Parcelable> posts)
    {
        currentPosts = new ArrayList<>();
        if (posts != null)
        {
            for (Parcelable post : posts)
            {
                if (post instanceof Post) currentPosts.add((Post) post);
            }
        }
        UserData.addListener(this);
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null)
        {
            location = UserData.getUser().getLocation();
            locationUpdated = true;
            if (currentPosts.size() == 0) PostInteractor.getClosestPosts(getRadius(), location, true, this);
        }
    }

    public void terminate()
    {
        view = null;
        UserData.removeListener(this);
    }



    public void refreshRecycler()
    {
        if (location != null) {
            PostInteractor.getClosestPosts(getRadius(), UserData.getUser().getLocation(),
                    true, this);
        } else {
            Toast.makeText(context, context.getString(R.string.post_refreshing_failed), Toast.LENGTH_SHORT).show();
            view.stopRefreshing();
        }
    }


    public void onLikePressed(Post post, boolean liked)
    {
        if (UserInteractor.getUserKey() != null) {
            if (liked) {
                PostInteractor.addPostToList(post, UserInteractor.getUserKey(), Constants.POSTS_LIKED_T);
                PostInteractor.modifyLikes(post.getPostKey(), true);
            }
            else {
                PostInteractor.removePostFromList(post, UserInteractor.getUserKey(), Constants.POSTS_LIKED_T);
                PostInteractor.modifyLikes(post.getPostKey(), false);
            }
        }
    }


    public void onSharePressed(Post post)
    {
        post.setShared(true);
        if (UserInteractor.getUserKey() != null) {
            PostInteractor.addPostToList(post, UserInteractor.getUserKey(), Constants.POSTS_SHARED_T);
            PostInteractor.modifyShares(post.getPostKey(), true);

            PostInteractor.publishPost(PostInteractor.createSharedPost(post, UserData.getUser().getBasicInfo(),
                    LocationUtils.getGeoPointFromMap(location)), new PostInteractor.OnUploadingPost() {
                @Override
                public void preparingUpload() {

                }

                @Override
                public void uploadDone() {
                    Toast.makeText(context, context.getString(R.string.post_shared_successful), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void onReportPressed(Post post, String report)
    {
        if (UserData.getUser() != null)
        {
            PostInteractor.reportPost(post, report, UserData.getUser().getBasicInfo());
            Toast.makeText(context, context.getString(R.string.post_report_sent), Toast.LENGTH_SHORT).show();
        }
    }


    public void onSavePressed(Post post)
    {
        if (UserInteractor.getUserKey() != null) {
            PostInteractor.addPostToList(post, UserInteractor.getUserKey(), Constants.POSTS_SAVED_T);
        }
    }


    public void onFavoritePressed(Post post)
    {
        if (UserInteractor.getUserKey() != null) {
            PostInteractor.addPostToList(post, UserInteractor.getUserKey(), Constants.POSTS_FAVORITES_T);
        }
    }

    public void onHidePressed(Post post)
    {
        view.removePost(post);

        if (UserInteractor.getUserKey() != null) {
            PostInteractor.setPostAsHided(post, UserInteractor.getUserKey());
        }
    }


    private int getRadius()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(Constants.PREFERENCES_RADIUS, 1);
    }





    //IMPLEMENT USERDATA INTERFACE
    @Override
    public void updateUserInfo() {
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null)
        {
            location = UserData.getUser().getLocation();
            if (!locationUpdated) {
                PostInteractor.getClosestPosts(getRadius(), UserData.getUser().getLocation(),
                        false, this);
            }
            locationUpdated = true;
        }
    }



    //IMPLEMENT POSTINTERACTOR INTERFACE
    @Override
    public void downloading(boolean isRefreshing) {
        if (!isRefreshing) view.enableLoadingView(true);
    }


    @Override
    public void downloadCompleted(ArrayList<Post> posts, boolean isRefreshing) {
        if (!isRefreshing) {
            view.enableLoadingView(false);
            currentPosts = posts;

            Collections.sort(currentPosts, (post, t1) -> t1.getTimestamp().compareTo(post.getTimestamp()));
            for (Post post : currentPosts) {
                if (!UserData.getHidedPosts().contains(post.getPostKey())){
                    view.addPost(post);
                }
            }
        } else {
            view.stopRefreshing();

            ArrayList<Post> newPosts = new ArrayList<>(posts);

            for (Post newPost : posts)
            {
                for (Post currentPost : currentPosts)
                {
                    if (newPost.compareTo(currentPost) == 0) newPosts.remove(newPost);
                }
            }

            if (newPosts.size() != 0){
                Collections.sort(newPosts, (post, t1) -> t1.getTimestamp().compareTo(post.getTimestamp()));
                currentPosts.addAll(0, newPosts);

                for (int i = newPosts.size() - 1; i >= 0; i--)
                {
                    if (!UserData.getHidedPosts().contains(newPosts.get(i).getPostKey())) {
                        view.addPostAtStart(newPosts.get(i));
                    }
                }
            }
        }
    }
}
