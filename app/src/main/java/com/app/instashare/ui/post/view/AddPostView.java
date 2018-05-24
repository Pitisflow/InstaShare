package com.app.instashare.ui.post.view;

import android.view.View;
import android.widget.ArrayAdapter;

import com.app.instashare.adapter.PostRVAdapter;

/**
 * Created by Pitisflow on 23/4/18.
 */

public interface AddPostView {

    void enablePublishButton(boolean enable);

    void enableShareAs(boolean enable);

    void setMaxLettersText(String text);

    void enableLoadingPost(boolean enable);

    void setAutoCompleteAdapter(ArrayAdapter<String> adapter);

    void setTagRecyclerAdapter(PostRVAdapter adapter);

    void addTagToAdapter(String tag);

    void deleteTagFromAdapter(String tag);

    void finishActivity();

    View getContentImage();

    View getContentText();
}
