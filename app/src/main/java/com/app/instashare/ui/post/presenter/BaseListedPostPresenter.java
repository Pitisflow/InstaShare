package com.app.instashare.ui.post.presenter;

import android.content.Context;
import android.os.Parcelable;
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.ClosePostsView;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.LocationUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Pitisflow on 29/5/18.
 */

public abstract class BaseListedPostPresenter {

    public Context context;
    public ClosePostsView view;
    public Map<String, Object> location;
    public boolean locationUpdated = false;

    public ArrayList<Post> currentPosts;


    public BaseListedPostPresenter(Context context, ClosePostsView view) {
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
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null)
        {
            location = UserData.getUser().getLocation();
            locationUpdated = true;
        }
    }


    public void terminate()
    {
        view = null;
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
            PostInteractor.setPostAsHidden(post, UserInteractor.getUserKey());
        }
    }


    public void onRemoveFromFavorites(Post post)
    {
        PostInteractor.removePostFromList(post, UserInteractor.getUserKey(), Constants.POSTS_FAVORITES_T);
    }

    public void onRemoveFromSaved(Post post)
    {
        PostInteractor.removePostFromList(post, UserInteractor.getUserKey(), Constants.POSTS_SAVED_T);
    }

    public void onRemoveFromHidden(Post post)
    {
        PostInteractor.removePostAsHidden(post, UserInteractor.getUserKey());
    }

}
