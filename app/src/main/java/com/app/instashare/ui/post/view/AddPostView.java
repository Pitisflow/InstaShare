package com.app.instashare.ui.post.view;

import android.view.View;

/**
 * Created by Pitisflow on 23/4/18.
 */

public interface AddPostView {

    void enablePublishButton(boolean enable);

    void setMaxLettersText(String text);

    View getContentImage();

    View getContentText();
}
