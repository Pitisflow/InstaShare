package com.app.instashare.ui.post.view;


/**
 * Created by Pitisflow on 24/5/18.
 */

public interface ClosePostsView {

    void enableLoadingView(boolean enable);

    void refreshRecycler();

    void addPost(Object post);

    void removePost(Object post);

    void addPostAtStart(Object post);

    void stopRefreshing();
}
