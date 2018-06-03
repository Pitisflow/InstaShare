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
import com.app.instashare.ui.user.model.User;
import com.app.instashare.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pitisflow on 24/5/18.
 */

public class ClosePostsPresenter extends BaseListedPostPresenter implements UserData.OnUserDataFetched,
        PostInteractor.OnDowloadingPosts, PostInteractor.OnDownloadingPostPerPost,
        PostInteractor.OnDownloadFollowingPosts{

    private ArrayList<Post> publicPosts;
    private ArrayList<Post> savedPosts;
    private ArrayList<Post> favoritedPosts;
    private ArrayList<Post> followingPosts;
    private ArrayList<Post> temp = new ArrayList<>();
    private boolean loadingMore = false;
    private AtomicInteger control = new AtomicInteger();
    private AtomicInteger controlNoPosts = new AtomicInteger();
    private int postShowing = ClosePostsFragment.SHOWING_PUBLIC;
    private long endAt = System.currentTimeMillis();
    private boolean isFollowing;


    public ClosePostsPresenter(Context context, ClosePostsView view) {
        super(context, view);
    }


    //********************************************
    //INITIALIZING PRESENTER AND TERMINATE
    //********************************************

    @Override
    public void onInitialize(ArrayList<Parcelable> posts) {
        super.onInitialize(posts);
        UserData.addListener(this);

        if (location != null && currentPosts.size() == 0){
            PostInteractor.getClosestPosts(getRadius(), UserData.getUser().getLocation(),
                    false, this);
        }
    }

    public void onInitialize(ArrayList<Parcelable> posts, boolean isFollowing)
    {
        currentPosts = new ArrayList<>();
        this.isFollowing = isFollowing;
        if (posts != null)
        {
            for (Parcelable post : posts)
            {
                if (post instanceof Post) currentPosts.add((Post) post);
            }
        }
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null)
        {
            location = UserData.getUser().getLocation();
            locationUpdated = true;
        }

        UserData.addListener(this);

        if (location != null && currentPosts.size() == 0){
            PostInteractor.downloadFollowingPosts(UserInteractor.getUserKey(), this);
        }
    }


    @Override
    public void terminate() {
        super.terminate();
        UserData.removeListener(this);
    }

    //********************************************
    //ACTIONS FROM ACTIVITY
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


    public void loadMoreToList()
    {
        view.setIsLoading(true);
        loadingMore = true;

        if (postShowing == ClosePostsFragment.SHOWING_FAVORITES) PostInteractor.getPostsFromList(UserInteractor.getUserKey(), Constants.POSTS_FAVORITES_T, endAt, this);
        else if (postShowing == ClosePostsFragment.SHOWING_SAVED) PostInteractor.getPostsFromList(UserInteractor.getUserKey(), Constants.POSTS_SAVED_T, endAt, this);
    }

    @Override
    public void onRemoveFromFavorites(Post post) {
        super.onRemoveFromFavorites(post);
        if (postShowing == ClosePostsFragment.SHOWING_FAVORITES) view.removePost(post);
        if (favoritedPosts != null && favoritedPosts.size() != 0) favoritedPosts.remove(post);
    }

    @Override
    public void onRemoveFromSaved(Post post) {
        super.onRemoveFromSaved(post);
        if (postShowing == ClosePostsFragment.SHOWING_SAVED) view.removePost(post);
        if (savedPosts != null && savedPosts.size() != 0) savedPosts.remove(post);
    }

    @Override
    public void onHidePressed(Post post) {
        super.onHidePressed(post);
        if (publicPosts != null) publicPosts.remove(post);
    }

    public void showPublicPosts()
    {
        postShowing = ClosePostsFragment.SHOWING_PUBLIC;

        if (publicPosts != null && publicPosts.size() != 0){
            currentPosts = new ArrayList<>(publicPosts);
            view.enableLoadingView(false, false, null);
            view.changePosts(new ArrayList<>(publicPosts));
        }
        else PostInteractor.getClosestPosts(getRadius(), location, false, this);
    }


    public void showFavoritedPosts()
    {
        postShowing = ClosePostsFragment.SHOWING_FAVORITES;
        view.setDownloadCompleted(false);
        view.setIsLoading(false);
        loadingMore = false;

        if (favoritedPosts != null && favoritedPosts.size() != 0) {
            currentPosts = new ArrayList<>(favoritedPosts);
            view.enableLoadingView(false, false, null);
            view.changePosts(new ArrayList<>(favoritedPosts));
        }
        else {
            endAt = System.currentTimeMillis();
            PostInteractor.getPostsFromList(UserInteractor.getUserKey(), Constants.POSTS_FAVORITES_T, endAt, this);
        }
    }


    public void showSavedPosts()
    {
        postShowing = ClosePostsFragment.SHOWING_SAVED;
        view.setDownloadCompleted(false);
        view.setIsLoading(false);
        loadingMore = false;

        if (savedPosts != null && savedPosts.size() != 0) {
            currentPosts = new ArrayList<>(savedPosts);
            view.enableLoadingView(false, false, null);
            view.changePosts(new ArrayList<>(savedPosts));
        }
        else {
            endAt = System.currentTimeMillis();
            PostInteractor.getPostsFromList(UserInteractor.getUserKey(), Constants.POSTS_SAVED_T, endAt, this);
        }
    }




    //********************************************
    //OTHERS
    //********************************************
    private int getRadius()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(Constants.PREFERENCES_RADIUS, 1);
    }

    private boolean getShowHiddenPosts()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(Constants.PREFERENCES_SHOW_HIDDEN, false);
    }

    private long setEndAt(ArrayList<Post> posts)
    {
        return posts.get(posts.size() - 1).getTimestamp() - 1;
    }

    public void setPostShowing(int postShowing) {
        this.postShowing = postShowing;
    }

    public void setSavedPosts(ArrayList<Post> savedPosts) {
        this.savedPosts = savedPosts;
        endAt = setEndAt(savedPosts);
    }

    public void setFavoritedPosts(ArrayList<Post> favoritedPosts) {
        this.favoritedPosts = favoritedPosts;
        endAt = setEndAt(favoritedPosts);
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
    }

    public void needReloadData()
    {
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null) {
            PostInteractor.getClosestPosts(getRadius(), UserData.getUser().getLocation(), false, this);
        }
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
                if (!isFollowing) PostInteractor.getClosestPosts(getRadius(), UserData.getUser().getLocation(),
                        false, this);

                if (isFollowing) {
                    PostInteractor.downloadFollowingPosts(UserInteractor.getUserKey(), this);
                }
            }
            locationUpdated = true;
        }
    }



    //**********************************************
    //IMPLEMENTS POSTINTERACTOR INTERFACE FOR PUBLIC
    //**********************************************
    @Override
    public void downloading(boolean isRefreshing) {
        if (!isRefreshing) {
            view.enableLoadingView(true, true, context.getString(R.string.post_loading));
            view.changePosts(new ArrayList<>());
        }
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
                if (!UserData.getHiddenPosts().contains(post.getPostKey()) || getShowHiddenPosts()) view.addPost(post);
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
                    if (!UserData.getHiddenPosts().contains(newPosts.get(i).getPostKey())) {
                        view.addPostAtStart(newPosts.get(i));
                    }
                }
            }
            publicPosts = new ArrayList<>(currentPosts);
        }
    }


    //********************************************
    //IMPLEMENTS POSTINTERACTOR INTERFACE FOR LIST
    //********************************************

    @Override
    public void downloading() {
        if (!loadingMore && view != null) {
            view.changePosts(new ArrayList<>());
            view.enableLoadingView(true, true, context.getString(R.string.post_loading));
        } else if (view != null) view.enableLoading(true);
    }

    @Override
    public void downloadNumber(int number) {
        control.getAndSet(number);
        temp = new ArrayList<>();

        if (number == 0 && !loadingMore && view != null){
            if (postShowing == ClosePostsFragment.SHOWING_FAVORITES) {
                view.enableLoadingView(true, false, context.getString(R.string.post_error_no_favs));
            }
            if (postShowing == ClosePostsFragment.SHOWING_SAVED) {
                view.enableLoadingView(true, false, context.getString(R.string.post_error_no_saved));
            }
        } else if (number == 0 && view != null) {
            view.setIsLoading(true);
            view.setDownloadCompleted(true);
            view.enableLoading(false);
        }
    }

    @Override
    public void downloadCompleted(Post post) {
        control.getAndSet(control.decrementAndGet());
        if (control.intValue() == 0 && view != null)
        {
            view.enableLoadingView(false, false, null);
            temp.add(post);
            Collections.sort(temp, (p, t1) -> t1.getTimestamp().compareTo(p.getTimestamp()));

            if (currentPosts != null && currentPosts.size() != 0 && !loadingMore)
            {
                currentPosts = new ArrayList<>(temp);
                view.changePosts(new ArrayList<>(currentPosts));
            } else
            {
                view.enableLoading(false);
                currentPosts.addAll(currentPosts.size(), temp);
                for (Post p : temp)
                {
                    view.addPost(p);
                }

                if (temp.size() < PostInteractor.LIMIT_POSTS) {
                    view.setIsLoading(true);
                    view.setDownloadCompleted(true);
                }
                else view.setIsLoading(false);
            }

            if (postShowing == ClosePostsFragment.SHOWING_FAVORITES) favoritedPosts = new ArrayList<>(currentPosts);
            if (postShowing == ClosePostsFragment.SHOWING_SAVED) savedPosts = new ArrayList<>(currentPosts);
            endAt = temp.get(temp.size() - 1).getTimestamp() - 1;
        } else {
            temp.add(post);
        }
    }


    //********************************************
    //IMPLEMENTS POSTINTERACTOR INTERFACE FOR LIST
    //********************************************

    @Override
    public void downloadingFollowing() {
        if (view != null) {
            view.enableLoadingView(true, true, context.getString(R.string.post_loading));
            view.changePosts(new ArrayList<>());
        }
    }

    @Override
    public void downloadFollowingNumber(int number) {
        control.getAndSet(number);
        controlNoPosts.getAndSet(number);
        temp = new ArrayList<>();
    }

    @Override
    public void downloadFollowingCompleted(ArrayList<Post> posts) {
        control.getAndSet(control.decrementAndGet());
        if (control.intValue() == 0 && view != null)
        {
            view.enableLoadingView(false, false, null);
            temp.addAll(posts);
            Collections.sort(temp, (p, t1) -> t1.getTimestamp().compareTo(p.getTimestamp()));

            if (currentPosts != null && currentPosts.size() != 0 && !loadingMore)
            {
                currentPosts = new ArrayList<>(temp);
                view.changePosts(new ArrayList<>(currentPosts));
            } else
            {
                view.enableLoading(false);
                currentPosts.addAll(currentPosts.size(), temp);
                for (Post p : temp)
                {
                    if (!UserData.getHiddenPosts().contains(p.getPostKey()) || getShowHiddenPosts()) view.addPost(p);
                }
            }

            followingPosts = new ArrayList<>(currentPosts);
            if (followingPosts.size() == 0) {
                view.enableLoadingView(false, false, null);
            }
        } else if (posts != null) temp.addAll(posts);
    }

    @Override
    public void noPosts() {
        controlNoPosts.getAndSet(controlNoPosts.decrementAndGet());

        if (controlNoPosts.intValue() == 0) {
            view.enableLoadingView(false, false, context.getString(R.string.post_error_no_post));
        }
    }
}