package com.app.instashare.ui.post.view;

import android.net.Uri;

import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.ui.post.model.Post;

/**
 * Created by Pitisflow on 1/5/18.
 */

public interface PreviewPostView {

    void enableTextUp(boolean enable);

    void enableTextDown(boolean enable);

    void enableContentImage(boolean enable);

    void enableTagsRecycler(boolean enable);

    void setTextUp(CharSequence text);

    void setTextDown(CharSequence text);

    void setContentImage(Uri uri);

    void setUserImage(String url);

    void setUserName(CharSequence userName);

    void setDate(String date);

    void setDistance(String distance);

    void setTagsRecyclerAdapter(PostRVAdapter tagsRecyclerAdapter);

    void openNewPostActivity(String postKey);

    void openNewUserActivity(String userKey);

    void setUserImageClick(boolean isClickable, String userKey);
}
