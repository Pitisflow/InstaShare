package com.app.instashare.ui.post.view;

import android.view.View;
import android.widget.ArrayAdapter;

import com.app.instashare.adapter.TagRVAdapter;

/**
 * Created by Pitisflow on 23/4/18.
 */

public interface AddPostView {

    void enablePublishButton(boolean enable);

    void enableShareAs(boolean enable);

    void setMaxLettersText(String text);

    void setAutoCompleteAdapter(ArrayAdapter<String> adapter);

    void setTagRecyclerAdapter(TagRVAdapter adapter);

    void addTagToAdapter(String tag);

    View getContentImage();

    View getContentText();
}
