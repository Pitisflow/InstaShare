package com.app.instashare.ui.post.view;

import android.net.Uri;

import com.app.instashare.adapter.PostRVAdapter;

/**
 * Created by Pitisflow on 1/5/18.
 */

public interface PreviewPostView {

    void enableTextUp(boolean enable);

    void enableTextDown(boolean enable);

    void enableContentImage(boolean enable);

    void enableTagsRecycler(boolean enable);

    void setTextUp(String text);

    void setTextDown(String text);

    void setContentImage(Uri uri);

    void setUserImage(String url);

    void setUserName(String userName);

    void setDate(String date);

    void setDistance(String distance);

    void setTagsRecyclerAdapter(PostRVAdapter tagsRecyclerAdapter);
}
