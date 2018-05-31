package com.app.instashare.ui.post.view;

import android.graphics.drawable.Drawable;

import com.app.instashare.ui.post.model.Comment;
import com.app.instashare.ui.post.model.Post;

/**
 * Created by Pitisflow on 30/5/18.
 */

public interface DetailPostView {
    void onPostDownloaded(Post post);

    void enableLikeButton(boolean enabled);

    void enableShareButton(boolean enabled);

    void enableAudioView(boolean enabled);

    void enableSendButton(boolean enabled);

    void enableLoading(boolean enabled);

    void enableSaveMenuItem(boolean enabled);

    void enableFavoriteMenuItem(boolean enabled);

    void setFavoriteMenuItemIcon(boolean pressed);

    void setSaveMenuItemIcon(boolean pressed);

    void setLikeButton(int color, Drawable drawable);

    void setShareButton(int color, Drawable drawable);

    void setLocalAudioFile(String file);

    void setPostLikes(String text);

    void setPostComments(String text);

    void addComment(Comment comment);

    void modifyComment(Comment comment);

    void deleteComment();

    void focusCommentET();

    void resetComment();
}
