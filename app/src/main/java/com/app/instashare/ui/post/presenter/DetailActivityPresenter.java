package com.app.instashare.ui.post.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.SymbolTable;
import android.media.MediaRecorder;
import android.widget.Button;
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.interactor.PostInteractor;
import com.app.instashare.interactor.UserInteractor;
import com.app.instashare.singleton.UserData;
import com.app.instashare.ui.post.model.Comment;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.view.DetailPostView;
import com.app.instashare.utils.AudioUtils;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.LocationUtils;
import com.app.instashare.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Pitisflow on 30/5/18.
 */

public class DetailActivityPresenter implements PostInteractor.OnDownloadSinglePost,
        UserData.OnUserDataFetched,
        PostInteractor.OnUploadingComment,
        PostInteractor.OnDownloadComments{

    private DetailPostView view;
    private Context context;


    private Map<String, Object> location;
    private Post post;
    private Comment uploadingComment;
    private ArrayList<Comment> currentComments;


    private MediaRecorder recorder;
    private String audioFile;


    public DetailActivityPresenter(DetailPostView view, Context context) {
        this.view = view;
        this.context = context;
    }


    //********************************************
    //INITIALIZING AND TERMINATE
    //********************************************

    public void onInitialize(Post post, String postKey)
    {
        UserData.addListener(this);
        if (UserData.getUser() != null) {
            location = UserData.getUser().getLocation();
        }

        if (post == null && postKey != null) {
            PostInteractor.downloadSinglePost(postKey, this);
        } else {
            checkPostStatus(post);
            this.post = post;
        }

        if (currentComments == null) {
            currentComments = new ArrayList<>();

            if (postKey != null) {
                PostInteractor.downloadCommentsFromPost(postKey, System.currentTimeMillis(), this);
            } else if (post != null) {
                PostInteractor.downloadCommentsFromPost(post.getPostKey(), System.currentTimeMillis(), this);
            }
        }


        view.enableAudioView(false);
    }

    public void terminate()
    {
        UserData.removeListener(this);

        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;

            File file = new File(audioFile);
            file.delete();
        }
        view = null;
    }



    //********************************************
    //ACTIONS FROM ACTIVITY
    //********************************************

    public void onLikeButtonPressed()
    {
        if (!post.isLiked())
        {
            PostInteractor.addPostToList(post, UserInteractor.getUserKey(), Constants.POSTS_LIKED_T);
            PostInteractor.modifyLikes(post.getPostKey(), true);
            post.setLiked(true);
            setButton(true, true);
        } else {
            PostInteractor.removePostFromList(post, UserInteractor.getUserKey(), Constants.POSTS_LIKED_T);
            PostInteractor.modifyLikes(post.getPostKey(), false);
            post.setLiked(false);
            setButton(false, true);
        }
    }

    public void onShareButtonPressed()
    {
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

        setButton(true, false);
    }

    public void onCommentButtonPressed()
    {
        view.focusCommentET();
    }


    public void onStartRecordingPressed()
    {
        File file = AudioUtils.makeAudioFile(context);

        if (file != null) {
            audioFile = file.toString();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(file.toString());

            try {
                recorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            recorder.start();
        }
    }

    public void onStopRecordingPressed()
    {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;

            view.setLocalAudioFile(audioFile);
        }
    }

    public void onDownloadMoreComments()
    {
        if (currentComments != null) {
            PostInteractor.downloadCommentsFromPost(post.getPostKey(), setEndAt(currentComments), this);
        }
    }


    public void onCommentPressed(String commentText, String commentAudioURL)
    {
        String comment = null;
        if (commentText != null) comment = commentText.trim();

        if ((comment != null && comment.length() != 0) || commentAudioURL != null) {
            Comment commentToSend = generateComment(comment, commentAudioURL);

            if (commentToSend != null){
                uploadingComment = commentToSend;
                PostInteractor.publishComment(commentToSend, post.getPostKey(), this);
            }
        }
    }


    //********************************************
    //OTHERS
    //********************************************

    private void checkPostStatus(Post post)
    {
        PostInteractor.checkPostOnList(post, UserInteractor.getUserKey(), Constants.POSTS_LIKED_T,
                new PostInteractor.OnCheckedList() {
                    @Override
                    public void isOnList() {
                        post.setLiked(true);
                        view.enableLikeButton(true);
                        setButton(true, true);
                    }

                    @Override
                    public void isNotOnlist() {
                        post.setLiked(false);
                        view.enableLikeButton(true);
                        setButton(false, true);
                    }
                });

        PostInteractor.checkPostOnList(post, UserInteractor.getUserKey(), Constants.POSTS_SHARED_T,
                new PostInteractor.OnCheckedList() {
                    @Override
                    public void isOnList() {
                        post.setShared(true);
                        view.enableShareButton(true);
                        setButton(true, false);
                    }

                    @Override
                    public void isNotOnlist() {
                        post.setShared(false);
                        view.enableShareButton(true);
                        setButton(false, false);
                    }
                });
    }


    private void setButton(boolean isPressed, boolean isLikeButton)
    {
        int color;
        Drawable drawable;

        if(isPressed) color = R.color.colorPrimary;
        else color = R.color.grayDark;

        if (isLikeButton) {
            if (isPressed) drawable = context.getDrawable(R.drawable.ic_thumb_up_black_24);
            else drawable = context.getDrawable(R.drawable.ic_outline_thumb_up_black_24);
        } else {
            drawable = context.getDrawable(R.drawable.ic_reply_black_24);
        }

        drawable = Utils.changeDrawableColor(drawable, color, context);

        if (isLikeButton) view.setLikeButton(context.getResources().getColor(color), drawable);
        else view.setShareButton(context.getResources().getColor(color), drawable);
    }


    private Comment generateComment(String commentText, String commentAudio)
    {
        if (UserData.getUser() != null && post != null)
        {
            Comment comment = new Comment();
            comment.setAudioURL(commentAudio);
            comment.setCommentText(commentText);
            comment.setTimestamp(System.currentTimeMillis());
            comment.setPostKey(post.getPostKey());
            comment.setUser(UserData.getUser().getBasicInfo());
            comment.setNew(true);

            return comment;
        } else return null;
    }


    private long setEndAt(ArrayList<Comment> comments)
    {
        return comments.get(comments.size() - 1).getTimestamp() - 1;
    }


    //********************************************
    //IMPLEMENTS USERDATA INTERFACE
    //********************************************

    @Override
    public void updateUserInfo() {
        if (UserData.getUser() != null && UserData.getUser().getLocation() != null) {
            location = UserData.getUser().getLocation();
        }
    }



    //********************************************
    //IMPLEMENTS POSTINTERACTOR INTERFACE (post)
    //********************************************

    @Override
    public void downloadCompleted(Post post) {
        this.post = post;

        if (this.post != null) {
            checkPostStatus(post);
            view.onPostDownloaded(post);
        }
    }

    //****************************************************
    //IMPLEMENTS POSTINTERACTOR INTERFACE (comment upload)
    //****************************************************

    @Override
    public void preparingUpload() {
        view.enableSendButton(false);
    }

    @Override
    public void uploadCompleted() {
        if (view != null) {
            view.resetComment();
            view.enableSendButton(true);
            view.addComment(uploadingComment);
        }

        Toast.makeText(context, context.getString(R.string.post_comment_upload_successful), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void uploadFailed() {
        if (view != null) view.enableSendButton(true);
        Toast.makeText(context, context.getString(R.string.post_comment_upload_audio_fail), Toast.LENGTH_SHORT).show();
    }


    //******************************************************
    //IMPLEMENTS POSTINTERACTOR INTERFACE (comments dowload)
    //******************************************************

    @Override
    public void preparingDownload() {
        view.enableLoading(true);
    }

    @Override
    public void downloadCompleted(ArrayList<Comment> comments) {
        if (view != null) view.enableLoading(false);

        for (int i = comments.size() - 1; i >= 0; i --)
        {
            currentComments.add(comments.get(i));
            if (view != null) view.addComment(comments.get(i));
        }
    }
}
