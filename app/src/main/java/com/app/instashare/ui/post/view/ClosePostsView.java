package com.app.instashare.ui.post.view;


import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Pitisflow on 24/5/18.
 */

public interface ClosePostsView {

    void enableLoadingView(boolean enable, boolean loading, String message);

    void refreshRecycler();

    void addPost(Object post);

    void removePost(Object post);

    void addPostAtStart(Object post);

    void changePosts(ArrayList<Object> posts);

    void stopRefreshing();
}
