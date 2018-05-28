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
import com.app.instashare.ui.post.fragment.ClosePostsFragment;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.ClosePostsView;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.LocationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pitisflow on 24/5/18.
 */

public class ClosePostsPresenter implements UserData.OnUserDataFetched,
        PostInteractor.OnDowloadingPosts, PostInteractor.OnDownloadingPostPerPost {

    private Context context;
    private ClosePostsView view;


    private ArrayList<Post> currentPosts;
    private ArrayList<Post> publicPosts;
    private ArrayList<Post> savedPosts;
    private ArrayList<Post> favoritedPosts;
    private ArrayList<Post> temp;
    private Map<String, Object> location;
    private boolean locationUpdated = false;
    private AtomicInteger control = new AtomicInteger();
    private int postShowing = ClosePostsFragment.SHOWING_PUBLIC;


    public ClosePostsPresenter(Context context, ClosePostsView view) {
        this.context = context;
        this.view = view;
    }


    //********************************************
    //INITIALIZING PRESENTER AND TERMINATE
    //********************************************
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



    //********************************************
    //ACTIONS FROM VIEW
    //********************************************
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


    public void showPublicPosts()
    {
        postShowing = ClosePostsFragment.SHOWING_PUBLIC;

        if (publicPosts != null && publicPosts.size() != 0){
            view.enableLoadingView(false, false, null);
            view.changePosts(new ArrayList<>(publicPosts));
        }
        else PostInteractor.getClosestPosts(getRadius(), location, false, this);
    }


    public void showFavoritedPosts()
    {
        postShowing = ClosePostsFragment.SHOWING_FAVORITES;

        if (favoritedPosts != null && favoritedPosts.size() != 0) {
            view.enableLoadingView(false, false, null);
            view.changePosts(new ArrayList<>(favoritedPosts));
        }
        else PostInteractor.getPostsFromList(UserInteractor.getUserKey(), Constants.POSTS_FAVORITES_T, System.currentTimeMillis(), this);
    }


    public void showSavedPosts()
    {
        postShowing = ClosePostsFragment.SHOWING_SAVED;

        if (savedPosts != null && savedPosts.size() != 0) {
            view.enableLoadingView(false, false, null);
            view.changePosts(new ArrayList<>(savedPosts));
        }
        else PostInteractor.getPostsFromList(UserInteractor.getUserKey(), Constants.POSTS_SAVED_T, System.currentTimeMillis(), this);
    }




    //********************************************
    //OTHERS
    //********************************************
    private int getRadius()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(Constants.PREFERENCES_RADIUS, 1);
    }





    //********************************************
    //IMPLEMENT USERDATA INTERFACE
    //********************************************
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



    //********************************************
    //IMPLEMENTS POSTINTERACTOR INTERFACES
    //********************************************
    @Override
    public void downloading(boolean isRefreshing) {
        if (!isRefreshing) view.enableLoadingView(true, true, context.getString(R.string.post_loading));
    }


    @Override
    public void downloadCompleted(ArrayList<Post> posts, boolean isRefreshing) {
        if (view != null && posts.size() == 0)
        {
            view.enableLoadingView(true, false, context.getString(R.string.post_error_no_close_posts));
        } else if (!isRefreshing && view != null) {
            view.enableLoadingView(false, false, null);
            currentPosts = posts;

            Collections.sort(currentPosts, (post, t1) -> t1.getTimestamp().compareTo(post.getTimestamp()));
            for (Post post : currentPosts) {
                if (!UserData.getHidedPosts().contains(post.getPostKey())){
                    view.addPost(post);
                }
            }
            publicPosts = new ArrayList<>(currentPosts);
        } else if (view != null){
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
            publicPosts = new ArrayList<>(currentPosts);
        }
    }

    @Override
    public void downloading() {
        view.changePosts(new ArrayList<>());
        view.enableLoadingView(true, true, context.getString(R.string.post_loading));
    }

    @Override
    public void downloadNumber(int number) {
        control.getAndSet(number);
        temp = new ArrayList<>();

        if (number == 0){
            if (postShowing == ClosePostsFragment.SHOWING_FAVORITES) {
                view.enableLoadingView(true, false, context.getString(R.string.post_error_no_favs));
            }
            if (postShowing == ClosePostsFragment.SHOWING_SAVED) {
                view.enableLoadingView(true, false, context.getString(R.string.post_error_no_saved));
            }
        }
    }

    @Override
    public void downloadCompleted(Post post) {
        control.getAndSet(control.decrementAndGet());
        if (control.intValue() == 0)
        {
            temp.add(post);
            Collections.sort(temp, (p, t1) -> t1.getTimestamp().compareTo(p.getTimestamp()));

            if (postShowing == ClosePostsFragment.SHOWING_FAVORITES) favoritedPosts = new ArrayList<>(temp);
            if (postShowing == ClosePostsFragment.SHOWING_SAVED) savedPosts = new ArrayList<>(temp);
            currentPosts = new ArrayList<>(temp);

            view.changePosts(new ArrayList<>(currentPosts));
            view.enableLoadingView(false, false, null);
        } else {
            temp.add(post);
        }
    }
}
