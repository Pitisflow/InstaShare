package com.app.instashare.ui.post.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.instashare.R;
import com.app.instashare.adapter.PostRVAdapter;
import com.app.instashare.custom.AudioBar;
import com.app.instashare.ui.post.model.Comment;
import com.app.instashare.ui.post.model.Post;
import com.app.instashare.ui.post.presenter.DetailActivityPresenter;
import com.app.instashare.ui.post.view.DetailPostView;
import com.app.instashare.utils.Constants;
import com.app.instashare.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Pitisflow on 30/5/18.
 */

public class DetailPostActivity extends PreviewPostActivity implements DetailPostView,
        PostRVAdapter.OnCommentInteraction {

    private static final String EXTRA_POST = "post";
    private static final String EXTRA_POST_KEY = "postKey";


    public static Intent newInstance(Context context, Post post){
        Intent intent = new Intent(context, DetailPostActivity.class);
        intent.putExtra(EXTRA_POST, post);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }


    public static Intent newInstance(Context context, String postKey){
        Intent intent = new Intent(context, DetailPostActivity.class);
        intent.putExtra(EXTRA_POST_KEY, postKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }




    private Button likeButton;
    private Button shareButton;
    private EditText commentET;
    private AudioBar commentAudioBar;
    private ImageButton commentAudioRecord;
    private ImageButton commentSend;
    private RecyclerView recyclerView;
    private PostRVAdapter adapter;
    private NestedScrollView nestedScrollView;

    private Snackbar snackbar;


    private DetailActivityPresenter presenter;
    private String postKey;
    private String audioState;
    private boolean isUploading = false;
    private boolean isLoading = false;
    private Parcelable recyclerState = null;
    private ArrayList<Parcelable> recyclerItemsState = null;


    private static final int PERMISSION_CODE_RECORD = 2000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null && getIntent().hasExtra(EXTRA_POST_KEY)) {
            postKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("post")) {
            setPost(savedInstanceState.getParcelable("post"));
        }
        if (savedInstanceState != null && savedInstanceState.containsKey("audioState")) {
            audioState = savedInstanceState.getString("audioState");
        }

        if (savedInstanceState != null)
        {
            recyclerItemsState = savedInstanceState.getParcelableArrayList("recyclerItems");
            recyclerState = savedInstanceState.getParcelable("recycler");
        }


        bindCommentButtonView();
        bindLikeButtonView();
        bindShareButtonView();
        bindCommentView();
        bindCommentsRecyclerView();
    }


    @Override
    protected void onStart() {
        super.onStart();

        presenter = new DetailActivityPresenter(this, this);
        presenter.onInitialize(getPost(), postKey);

        if (audioState != null) {
            commentAudioBar.setVisibility(View.VISIBLE);
            commentAudioBar.setAsyncFile(audioState);
        }

        if (recyclerItemsState != null && recyclerState != null) {
            for (Object comment : recyclerItemsState) {
                ((PostRVAdapter) recyclerView.getAdapter()).addCard(comment, Constants.CARD_POST_COMMENT);
            }
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
            commentAudioRecord.setEnabled(true);
        }
        presenter.terminate();
        presenter = null;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (getPost() != null) {
            outState.putParcelable("post", getPost());
        }

        if (adapter != null)
        {
            ArrayList<Comment> comments = new ArrayList<>();
            for (Object comment : adapter.getItemList())
            {
                if (comment instanceof Comment) comments.add((Comment) comment);
            }

            outState.putParcelableArrayList("recyclerItems", comments);
            outState.putParcelable("recycler", recyclerView.getLayoutManager().onSaveInstanceState());
        }


        if (audioState != null && commentAudioBar.getVisibility() == View.VISIBLE && !isUploading) {
            outState.putString("audioState", audioState);
        } else audioState = null;
    }



    //********************************************
    //BIND VIEWS
    //********************************************

    private void bindShareButtonView()
    {
        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener((view) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailPostActivity.this);

            builder.setCancelable(false);
            builder.setMessage(getString(R.string.post_share_message));
            builder.setNegativeButton(getString(R.string.post_share_option_cancel), null);
            builder.setPositiveButton(getString(R.string.post_share_option_ok),
                    (dialogInterface, i) -> presenter.onShareButtonPressed());

            if (!getPost().isShared()) builder.show();
            else Toast.makeText(getApplicationContext(), getString(R.string.post_shared_already), Toast.LENGTH_SHORT).show();
        });
    }


    private void bindLikeButtonView()
    {
        likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener((view) -> presenter.onLikeButtonPressed());
    }


    private void bindCommentButtonView()
    {
        Button commentButton = findViewById(R.id.commentButton);
        commentButton.setOnClickListener((view) -> presenter.onCommentButtonPressed());

        Drawable drawable = getDrawable(R.drawable.ic_mode_comment_black_24);
        drawable = Utils.changeDrawableColor(drawable, R.color.grayDark, getApplicationContext());
        commentButton.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
    }


    private void bindCommentView()
    {
        Drawable drawableSend = getDrawable(R.drawable.ic_send_black_24);
        Drawable drawableRecord = getDrawable(R.drawable.ic_keyboard_voice_black_24);
        drawableSend = Utils.changeDrawableColor(drawableSend, R.color.colorPrimary, getApplicationContext());
        drawableRecord = Utils.changeDrawableColor(drawableRecord, R.color.colorPrimary, getApplicationContext());


        commentET = findViewById(R.id.commentET);
        commentAudioBar = findViewById(R.id.audioBar);
        commentAudioRecord = findViewById(R.id.recordAudio);
        commentSend = findViewById(R.id.sendComment);



        commentAudioBar.setSeekBarColor(R.color.colorPrimary);
        commentAudioBar.setIconsColor(R.color.colorPrimary);
        commentAudioBar.isCancellable(true);

        commentAudioRecord.setImageDrawable(drawableRecord);
        commentSend.setImageDrawable(drawableSend);

        commentAudioRecord.setOnClickListener((view) -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    startRecording();
                } else {
                    ActivityCompat.requestPermissions(DetailPostActivity.this, new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE_RECORD);
                }
            }


        });

        commentSend.setOnClickListener((view) -> presenter.onCommentPressed(commentET.getText().toString(), audioState));
    }


    private void bindCommentsRecyclerView()
    {
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        adapter = new PostRVAdapter(getApplicationContext());
        adapter.setCommentListener(this);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);


        nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(v.getChildAt(v.getChildCount() - 1) != null)
            {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY)
                {
                    int visibleItemCount = (recyclerView.getLayoutManager()).getChildCount();
                    int totalItemCount = (recyclerView.getLayoutManager()).getChildCount();
                    int pastVisiblesItems = (recyclerView.getLayoutManager()).getChildCount();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading)
                    {
                        presenter.onDownloadMoreComments();
                    }
                }
            }
        });
    }





    //********************************************
    //OTHERS
    //********************************************

    private void startRecording()
    {
        commentAudioRecord.setEnabled(false);
        presenter.onStartRecordingPressed();
        Toast.makeText(getApplicationContext(), getString(R.string.post_comment_recording_toast), Toast.LENGTH_SHORT).show();

        snackbar = Snackbar.make(findViewById(R.id.content), getString(R.string.post_comment_recording_snackbar), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.post_comment_recording_snackbar_stop), view -> {

                    commentAudioRecord.setEnabled(true);
                    presenter.onStopRecordingPressed();
                });

        snackbar.show();
    }




    //********************************************
    //PERMISSIONS
    //********************************************

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE_RECORD && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            startRecording();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.post_comment_recording_permissions), Toast.LENGTH_SHORT).show();
        }

    }


    //********************************************
    //IMPLEMENTS VIEW
    //********************************************

    @Override
    public void onPostDownloaded(Post post) {
        setPost(post);
        super.onStart();
    }

    @Override
    public void enableLikeButton(boolean enabled) {
        likeButton.setEnabled(enabled);
    }

    @Override
    public void enableShareButton(boolean enabled) {
        shareButton.setEnabled(enabled);
    }

    @Override
    public void enableAudioView(boolean enabled) {
        if (enabled) commentAudioBar.setVisibility(View.VISIBLE);
        else commentAudioBar.setVisibility(View.GONE);
    }

    @Override
    public void enableSendButton(boolean enabled) {
        commentSend.setEnabled(enabled);
        isUploading = !enabled;
    }

    @Override
    public void enableLoading(boolean enabled) {
        if (enabled) {
            adapter.addCard(null, Constants.CARD_LOADING);
            isLoading = true;
        } else {
            adapter.removeLastCard();
            isLoading = false;
        }
    }

    @Override
    public void setLikeButton(int color, Drawable drawable) {
        likeButton.setTextColor(color);
        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
    }

    @Override
    public void setShareButton(int color, Drawable drawable) {
        shareButton.setTextColor(color);
        shareButton.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
    }

    @Override
    public void setLocalAudioFile(String file) {
        audioState = file;
        commentAudioBar.setVisibility(View.VISIBLE);
        commentAudioBar.setAsyncFile(file);
    }

    @Override
    public void addComment(Comment comment) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }

        adapter.addCard(comment, Constants.CARD_POST_COMMENT);
        if (comment.isNew()) nestedScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void focusCommentET() {
        commentET.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(this.getCurrentFocus(), 0);
    }

    @Override
    public void resetComment() {
        commentAudioBar.reset();
        commentET.setText("");
        audioState = null;
    }

    //********************************************
    //IMPLEMENTS POSTRVADAPTER
    //********************************************

    @Override
    public void onOptionsClicked(Comment comment) {
        System.out.println("OPTIONS");
    }

    @Override
    public void onUserClicked(String userKey) {
        System.out.println("USER: " + userKey);
    }
}
