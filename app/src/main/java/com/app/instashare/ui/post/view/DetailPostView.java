package com.app.instashare.ui.post.view;

import android.graphics.drawable.Drawable;

import com.app.instashare.ui.post.model.Post;

/**
 * Created by Pitisflow on 30/5/18.
 */

public interface DetailPostView {
    void onPostDownloaded(Post post);

    void enableLikeButton(boolean enabled);

    void enableShareButton(boolean enabled);

    void enableAudioView(boolean enabled);

    void setLikeButton(int color, Drawable drawable);

    void setShareButton(int color, Drawable drawable);

    void setLocalAudioFile(String file);

    void focusCommentET();
}
